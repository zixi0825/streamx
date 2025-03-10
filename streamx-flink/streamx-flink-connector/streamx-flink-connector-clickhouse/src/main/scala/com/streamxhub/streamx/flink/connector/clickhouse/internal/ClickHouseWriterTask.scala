/*
 * Copyright (c) 2019 The StreamX Project
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.streamxhub.streamx.flink.connector.clickhouse.internal

import com.streamxhub.streamx.common.util.Logger
import com.streamxhub.streamx.flink.connector.clickhouse.conf.ClickHouseConfig
import com.streamxhub.streamx.flink.connector.failover.{FailoverWriter, SinkRequest}
import io.netty.handler.codec.http.HttpHeaders
import org.asynchttpclient.{AsyncHttpClient, ListenableFuture, Request, Response}

import java.util.concurrent.{BlockingQueue, ExecutorService, TimeUnit}
import scala.util.Try
import scala.collection.JavaConversions._

case class ClickHouseWriterTask(id: Int,
                                clickHouseConf: ClickHouseConfig,
                                asyncHttpClient: AsyncHttpClient,
                                queue: BlockingQueue[SinkRequest],
                                callbackService: ExecutorService) extends Runnable with AutoCloseable with Logger {

  @volatile var isWorking = false

  val failoverWriter: FailoverWriter = new FailoverWriter(clickHouseConf.storageType, clickHouseConf.getFailoverConfig)

  override def run(): Unit = try {
    isWorking = true
    logInfo(s"Start writer task, id = $id")
    while (isWorking || queue.nonEmpty) {
      val req = queue.poll(300, TimeUnit.MILLISECONDS)
      if (req != null) {
        send(req)
      }
    }
  } catch {
    case e: Exception =>
      logError("Error while inserting data", e)
      throw new RuntimeException(e)
  } finally {
    logInfo(s"Task id = $id is finished")
  }

  def send(sinkRequest: SinkRequest): Unit = {
    val request = buildRequest(sinkRequest)
    logDebug(s"Ready to load data to ${sinkRequest.table}, size = ${sinkRequest.size}")
    val whenResponse = asyncHttpClient.executeRequest(request)
    val callback = respCallback(whenResponse, sinkRequest)
    whenResponse.addListener(callback, callbackService)
  }

  def buildRequest(sinkRequest: SinkRequest): Request = {
    val query = s"INSERT INTO ${sinkRequest.table} VALUES ${sinkRequest.records.mkString(",")}"
    val host = clickHouseConf.getRandomHostUrl
    val builder = asyncHttpClient
      .preparePost(host)
      .setRequestTimeout(clickHouseConf.timeout)
      .setHeader(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=utf-8")
      .setBody(query)

    if (clickHouseConf.credentials != null) {
      builder.setHeader(HttpHeaders.Names.AUTHORIZATION, "Basic " + clickHouseConf.credentials)
    }
    builder.build
  }

  def respCallback(whenResponse: ListenableFuture[Response], sinkRequest: SinkRequest): Runnable = new Runnable {
    override def run(): Unit = {
      Try(whenResponse.get()).getOrElse(null) match {
        case null =>
          logError(s"""Error ClickHouseSink executing callback, params = $clickHouseConf,can not get Response. """)
          handleFailedResponse(null, sinkRequest)
        case resp if !clickHouseConf.successCode.contains(resp.getStatusCode) =>
          logError(s"Error ClickHouseSink executing callback, params = $clickHouseConf, StatusCode = ${resp.getStatusCode} ")
          handleFailedResponse(resp, sinkRequest)
        case _ =>
      }
    }
  }

  /**
   * if send data to ClickHouse Failed, retry maxRetries, if still failed,flush data to failoverStorage
   *
   * @param response
   * @param sinkRequest
   */
  def handleFailedResponse(response: Response, sinkRequest: SinkRequest): Unit = {
    if (sinkRequest.attemptCounter > clickHouseConf.maxRetries) {
      logWarn(s"""Failed to send data to ClickHouse, cause: limit of attempts is exceeded. ClickHouse response = $response. Ready to flush data to ${clickHouseConf.storageType}""")
      failoverWriter.write(sinkRequest)
      logInfo(s"failover Successful, StorageType = ${clickHouseConf.storageType}, size = ${sinkRequest.size}")
    } else {
      sinkRequest.incrementCounter()
      logWarn(s"Next attempt to send data to ClickHouse, targetTable = ${sinkRequest.table}, buffer size = ${sinkRequest.size}, current attempt num = ${sinkRequest.attemptCounter}, max attempt num = ${clickHouseConf.maxRetries}, response = $response")
      queue.put(sinkRequest)
    }
  }


  override def close(): Unit = {
    isWorking = false
    failoverWriter.close()
  }
}
