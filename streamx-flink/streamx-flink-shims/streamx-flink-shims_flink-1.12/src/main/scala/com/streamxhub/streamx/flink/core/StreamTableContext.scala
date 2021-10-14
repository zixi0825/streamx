/*
 * Copyright (c) 2019 The StreamX Project
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.streamxhub.streamx.flink.core

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.table.descriptors.{ConnectorDescriptor, StreamTableDescriptor}

/**
 * 融合了流(stream)和table的api
 *
 * @param parameter
 * @param streamEnv
 * @param tableEnv
 */
class StreamTableContext(override val parameter: ParameterTool,
                         private val streamEnv: StreamExecutionEnvironment,
                         private val tableEnv: StreamTableEnvironment) extends FlinkStreamTableTrait(parameter, streamEnv, tableEnv) {

  /**
   * for scala
   *
   * @param args
   */
  def this(args: (ParameterTool, StreamExecutionEnvironment, StreamTableEnvironment)) = this(args._1, args._2, args._3)

  /**
   * for Java
   *
   * @param args
   */
  def this(args: StreamTableEnvConfig) = this(FlinkTableInitializer.initJavaStreamTable(args))


  @Deprecated override def connect(connectorDescriptor: ConnectorDescriptor): StreamTableDescriptor = tableEnv.connect(connectorDescriptor)


}
