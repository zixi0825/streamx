<template>
  <div>
    <a-tabs type="card" class="setting">
      <a-tab-pane key="system" tab="System Setting">
        <a-card
          :bordered="false"
          class="system_setting">
          <a-list>
            <a-list-item v-for="(item,index) in settings" :key="index">
              <a-list-item-meta style="width: 50%">
                <svg-icon class="avatar" name="maven" size="large" slot="avatar" v-if="item.key === 'streamx.maven.central.repository'"></svg-icon>
                <svg-icon class="avatar" name="user" size="large" slot="avatar" v-if="item.key === 'streamx.maven.auth.user'"></svg-icon>
                <svg-icon class="avatar" name="mvnpass" size="large" slot="avatar" v-if="item.key === 'streamx.maven.auth.password'"></svg-icon>
                <svg-icon class="avatar" name="http" size="large" slot="avatar" v-if="item.key === 'streamx.console.webapp.address'"></svg-icon>
                <svg-icon class="avatar" name="host" size="large" slot="avatar" v-if="item.key === 'alert.email.host'"></svg-icon>
                <svg-icon class="avatar" name="port" size="large" slot="avatar" v-if="item.key === 'alert.email.port'"></svg-icon>
                <svg-icon class="avatar" name="mail" size="large" slot="avatar" v-if="item.key === 'alert.email.from'"></svg-icon>
                <svg-icon class="avatar" name="user" size="large" slot="avatar" v-if="item.key === 'alert.email.userName'"></svg-icon>
                <svg-icon class="avatar" name="keys" size="large" slot="avatar" v-if="item.key === 'alert.email.password'"></svg-icon>
                <svg-icon class="avatar" name="ssl" size="large" slot="avatar" v-if="item.key === 'alert.email.ssl'"></svg-icon>
                <svg-icon class="avatar" name="docker" size="large" slot="avatar" v-if="item.key === 'docker.register.address'"></svg-icon>
                <svg-icon class="avatar" name="auth" size="large" slot="avatar" v-if="item.key === 'docker.register.user'"></svg-icon>
                <svg-icon class="avatar" name="password" size="large" slot="avatar" v-if="item.key === 'docker.register.password'"></svg-icon>
                <span slot="title">{{ item.title }}</span>
                <span slot="description">{{ item.description }}</span>
              </a-list-item-meta>
              <div class="list-content" style="width: 50%">
                <div class="list-content-item" style="width: 100%">
                  <template v-if="item.type === 1">
                    <input
                      :type="item.key === 'streamx.maven.auth.password' ? 'password': 'text'"
                      v-if="item.editable"
                      :value="item.value"
                      :class="item.key.replace(/\./g,'_')"
                      class="ant-input"/>
                    <div v-else style="width: 100%;text-align: right">
                      <span v-if="item.key === 'streamx.maven.auth.password' && item.value !== null"> ******** </span>
                      <span v-else>{{ item.value }}</span>
                    </div>
                  </template>
                  <template v-else>
                    <a-switch
                      checked-children="ON"
                      un-checked-children="OFF"
                      style="float: right;margin-right: 30px"
                      :default-checked="item.value === 'true'"
                      @change="handleSwitch(item)" />
                  </template>
                </div>
              </div>
              <div slot="actions" v-if="item.type === 1">
                <a v-if="!item.submitting" @click="handleEdit(item)">Edit</a>
                <a v-else @click="handleSubmit(item)">Submit</a>
              </div>
            </a-list-item>
          </a-list>
        </a-card>
      </a-tab-pane>
      <a-tab-pane key="flink" tab="Flink Home">
        <a-card
          :bordered="false"
          class="system_setting">
          <div
            v-permit="'project:create'">
            <a-button
              type="dashed"
              style="width: 100%;margin-top: 20px"
              icon="plus"
              @click="handleFlinkFormVisible(true)">
              Add New
            </a-button>
          </div>
          <a-list>
            <a-list-item v-for="(item,index) in flinks" :key="index">
              <a-list-item-meta style="width: 60%">
                <svg-icon class="avatar" name="flink" size="large" slot="avatar"></svg-icon>
                <span slot="title">{{ item.flinkName }}</span>
                <span slot="description">{{ item.description }}</span>
              </a-list-item-meta>

              <div class="list-content" style="width: 40%">
                <div class="list-content-item" style="width: 60%">
                  <span>Flink Home</span>
                  <p style="margin-top: 10px">
                    {{ item.flinkHome }}
                  </p>
                </div>
                <div
                  class="list-content-item"
                  style="width: 30%">
                  <span>Default</span>
                  <p style="margin-top: 10px">
                    <a-switch :disabled="item.isDefault" @click="handleSetDefault(item)" v-model="item.isDefault">
                      <a-icon slot="checkedChildren" type="check" />
                      <a-icon slot="unCheckedChildren" type="close"/>
                    </a-switch>
                  </p>
                </div>
              </div>

              <div slot="actions">
                <a @click="handleEditFlink(item)">Edit</a>
                <a-divider type="vertical" />
                <a @click="handleFlinkConf(item)">Flink Conf</a>
              </div>

            </a-list-item>
          </a-list>
        </a-card>
      </a-tab-pane>
      <a-tab-pane key="cluster" tab="Flink Cluster">
        <a-card
          :bordered="false"
          class="system_setting">
          <div
            v-permit="'project:create'">
            <a-button
              type="dashed"
              style="width: 100%;margin-top: 20px"
              icon="plus"
              @click="handleClusterFormVisible(true)">
              Add New
            </a-button>
          </div>
          <a-list>
            <a-list-item v-for="(item,index) in clusters" :key="index">
              <a-list-item-meta style="width: 60%">
                <svg-icon class="avatar" name="flink" size="large" slot="avatar"></svg-icon>
                <span slot="title">{{ item.clusterName }}</span>
                <span slot="description">{{ item.description }}</span>
              </a-list-item-meta>

              <div class="list-content" style="width: 40%">
                <div class="list-content-item" style="width: 60%">
                  <span>Address</span>
                  <p style="margin-top: 10px">
                    {{ item.address }}
                  </p>
                </div>
              </div>

              <div slot="actions">
                <a @click="handleEditCluster(item)">Edit</a>
              </div>

            </a-list-item>
          </a-list>
        </a-card>
      </a-tab-pane>
    </a-tabs>

    <a-drawer
      :mask-closable="false"
      width="calc(100% - 40%)"
      placement="right"
      :visible="flinkConfVisible"
      :centered="true"
      :keyboard="false"
      :body-style="{ paddingBottom: '80px' }"
      title="Flink Conf"
      @close="handleCloseConf()">
      <a-col style="font-size: 0.9rem">
        <div style="padding-bottom: 15px">
          Flink Home: &nbsp;&nbsp; {{ flinkHome }}
        </div>
        <div>
          Flink Conf:
          <div style="padding: 15px 0">
            <div id="conf"></div>
            <a-button
              type="primary"
              style="float:right;margin-top: 10px;margin-right: 130px"
              @click.native="handleSync">
              <a-icon type="sync" />
              Sync Conf
            </a-button>
          </div>
        </div>
      </a-col>
    </a-drawer>

    <a-modal
      v-model="flinkFormVisible">
      <template
        slot="title">
        <svg-icon
          slot="icon"
          name="flink"/>
        Add Flink
      </template>

      <a-form
        :form="flinkForm">
        <a-form-item
          label="Flink Name"
          style="margin-bottom: 10px"
          :label-col="{lg: {span: 7}, sm: {span: 7}}"
          :wrapper-col="{lg: {span: 16}, sm: {span: 4} }">
          <a-input
            type="text"
            placeholder="Please enter flink name"
            v-decorator="['flinkName',{ rules: [{ required: true } ]}]"/>
          <span
            class="conf-switch"
            style="color:darkgrey">the flink name, e.g: flink-1.12 </span>
        </a-form-item>

        <a-form-item
          label="Flink Home"
          :label-col="{lg: {span: 7}, sm: {span: 7}}"
          :wrapper-col="{lg: {span: 16}, sm: {span: 4} }">
          <a-input
            type="text"
            placeholder="Please enter flink home"
            v-decorator="['flinkHome',{ rules: [{ required: true } ]}]"/>
          <span
            class="conf-switch"
            style="color:darkgrey">The absolute path of the FLINK_HOME</span>
        </a-form-item>

        <a-form-item
          label="Description"
          :label-col="{lg: {span: 7}, sm: {span: 7}}"
          :wrapper-col="{lg: {span: 16}, sm: {span: 4} }">
          <a-textarea
            rows="4"
            name="description"
            placeholder="Please enter description"
            v-decorator="['description']"/>
        </a-form-item>

      </a-form>

      <template slot="footer">
        <a-button
          key="back"
          @click="handleFlinkFormVisible(false)">
          Cancel
        </a-button>
        <a-button
          key="submit"
          @click="handleSubmitFlink"
          type="primary">
          Submit
        </a-button>
      </template>
    </a-modal>

    <a-modal
      v-model="flinkClusterVisible">
      <template
        slot="title">
        <svg-icon
          slot="icon"
          name="flink"/>
        Add Cluster
      </template>

      <a-form
        :form="clusterForm">
        <a-form-item
          label="Cluster Name"
          style="margin-bottom: 10px"
          :label-col="{lg: {span: 7}, sm: {span: 7}}"
          :wrapper-col="{lg: {span: 16}, sm: {span: 4} }">
          <a-input
            type="text"
            placeholder="Please enter cluster name"
            v-decorator="['clusterName',{ rules: [{ required: true } ]}]"/>
          <span
            class="conf-switch"
            style="color:darkgrey">the cluster name, e.g: my Cluster </span>
        </a-form-item>

        <a-form-item
          label="Address"
          :label-col="{lg: {span: 7}, sm: {span: 7}}"
          :wrapper-col="{lg: {span: 16}, sm: {span: 4} }">
          <a-textarea
            rows="4"
            name="address"
            placeholder="Please enter cluster address,  e.g: http://host:port,http://host1:port2"
            v-decorator="['address',{ rules: [{ required: true } ]}]"/>
          <span
            class="conf-switch"
            style="color:darkgrey">cluster address,multiple addresses use "," split</span>
        </a-form-item>

        <a-form-item
          label="Description"
          :label-col="{lg: {span: 7}, sm: {span: 7}}"
          :wrapper-col="{lg: {span: 16}, sm: {span: 4} }">
          <a-textarea
            rows="4"
            name="description"
            placeholder="Please enter description"
            v-decorator="['description']"/>
        </a-form-item>

      </a-form>

      <template slot="footer">
        <a-button
          key="back"
          @click="handleClusterFormVisible(false)">
          Cancel
        </a-button>
        <a-button
          key="submit"
          @click="handleSubmitCluster"
          type="primary">
          Submit
        </a-button>
      </template>
    </a-modal>

  </div>
</template>

<script>
import {all, update } from '@api/setting'
import {
  list as listFlink,
  create as createFlink,
  get as getFlink,
  update as updateFlink,
  exists as existsEnv,
  setDefault,
  sync
} from '@/api/flinkEnv'

import {
  list as listCluster,
  create as createCluster,
  get as getCluster,
  update as updateCluster,
  check as checkCluster, check
} from '@/api/flinkCluster'

import SvgIcon from '@/components/SvgIcon'
import monaco from '@/views/flink/app/Monaco.yaml'

export default {
  name: 'Setting',
  components: { SvgIcon },
  data() {
    return {
      settings: [],
      flinks: [],
      clusters: [],
      flinkName: null,
      flinkHome: null,
      flinkConf: null,
      versionId: null,
      clusterId: null,
      flinkConfVisible: false,
      flinkFormVisible: false,
      flinkClusterVisible: false,
      editor: null,
      flinkForm: null,
      clusterForm: null
    }
  },

  computed: {
    myTheme() {
      return this.$store.state.app.theme
    }
  },

  mounted() {
    this.flinkForm = this.$form.createForm(this)
    this.clusterForm = this.$form.createForm(this)
    this.handleSettingAll()
    this.handleFlinkAll()
    this.handleClusterAll()
  },

  methods: {
    getOption() {
      return {
        theme: this.ideTheme(),
        language: 'yaml',
        selectOnLineNumbers: false,
        foldingStrategy: 'indentation', // 代码分小段折叠
        overviewRulerBorder: false, // 不要滚动条边框
        autoClosingBrackets: true,
        tabSize: 2, // tab 缩进长度
        readOnly: true,
        inherit: true,
        scrollBeyondLastLine: false,
        lineNumbersMinChars: 5,
        lineHeight: 24,
        automaticLayout: true,
        cursorBlinking: 'line',
        cursorStyle: 'line',
        cursorWidth: 3,
        renderFinalNewline: true,
        renderLineHighlight: 'all',
        quickSuggestionsDelay: 100,  //代码提示延时
        scrollbar: {
          useShadows: false,
          vertical: 'visible',
          horizontal: 'visible',
          horizontalSliderSize: 5,
          verticalSliderSize: 5,
          horizontalScrollbarSize: 15,
          verticalScrollbarSize: 15
        }
      }
    },

    handleSettingAll() {
      all({}).then((resp) => {
        this.settings = resp.data
      })
    },

    handleEdit(setting) {
      if (!setting.editable) {
        setting.submitting = true
      }
      setting.editable = !setting.editable
    },

    handleSubmit(setting) {
      setting.submitting = false
      setting.editable = false
      const className = setting.key.replace(/\./g, '_')
      const elem = document.querySelector('.' + className)
      const value = elem.value
      update({
        key: setting.key,
        value: value
      }).then((resp) => {
        this.handleSettingAll()
      })
    },

    handleFlinkFormVisible(flag) {
      this.versionId = null
      this.flinkFormVisible = flag
      this.flinkForm.resetFields()
    },

    handleEditFlink(item) {
      this.versionId = item.id
      this.flinkFormVisible = true
      this.$nextTick(()=>{
        this.flinkForm.setFieldsValue({
          'flinkName': item.flinkName,
          'flinkHome': item.flinkHome,
          'description': item.description || null
        })
      })
    },

    handleClusterFormVisible(flag) {
      this.clusterId = null
      this.flinkClusterVisible = flag
      this.clusterForm.resetFields()
    },

    handleEditCluster(item) {
      this.clusterId = item.id
      this.flinkClusterVisible = true
      this.$nextTick(()=>{
        this.clusterForm.setFieldsValue({
          'clusterName': item.clusterName,
          'address': item.address,
          'description': item.description || null
        })
      })
    },

    handleFlinkAll() {
      listFlink({}).then((resp)=>{
        this.flinks = resp.data
      })
    },

    handleClusterAll() {
      listCluster({}).then((resp)=>{
        this.clusters = resp.data
      })
    },

    handleSubmitFlink(e) {
      e.preventDefault()
      this.flinkForm.validateFields((err, values) => {
        if (!err) {
          existsEnv({
            id: this.versionId,
            flinkName: values.flinkName,
            flinkHome: values.flinkHome
          }).then((resp)=>{
            if (resp.data) {
              if (this.versionId == null) {
                createFlink(values).then((resp)=>{
                  if (resp.data) {
                    this.flinkFormVisible = false
                    this.handleFlinkAll()
                  } else {
                    this.$swal.fire(
                        'Failed',
                        resp['message'].replaceAll(/\[StreamX]/g,''),
                        'error'
                    )
                  }
                })
              } else {
                updateFlink({
                  id: this.versionId,
                  flinkName: values.flinkName,
                  flinkHome: values.flinkHome,
                  description: values.description || null
                }).then((resp)=>{
                  if (resp.data) {
                    this.flinkFormVisible = false
                    this.$swal.fire({
                      icon: 'success',
                      title: values.flinkName.concat(' update successful!'),
                      showConfirmButton: false,
                      timer: 2000
                    })
                    this.handleFlinkAll()
                  } else {
                    this.$swal.fire(
                      'Failed',
                      resp['message'].replaceAll(/\[StreamX]/g, ''),
                      'error'
                    )
                  }
                })
              }
            } else {
              if (resp.status === 'error') {
                this.$swal.fire(
                  'Failed',
                  'can no found flink-dist or found multiple flink-dist, FLINK_HOME error.',
                  'error'
                )
              } else {
                this.$swal.fire(
                  'Failed',
                  'flink name is already exists',
                  'error'
                )
              }
            }
          })
        }
      })
    },

    handleSubmitCluster(e) {
      e.preventDefault()
      this.clusterForm.validateFields((err, values) => {
        if (!err) {
          checkCluster({
            id: this.clusterId,
            clusterName: values.clusterName,
            address: values.address
          }).then((resp)=> {
            if (resp.data === 'success') {
              if (this.clusterId == null) {
                createCluster({
                  clusterName: values.clusterName,
                  address: values.address,
                  description: values.description || null
                }).then((resp)=>{
                  if (resp.data) {
                    this.flinkClusterVisible = false
                    this.handleClusterAll()
                  } else {
                    this.$swal.fire(
                        'Failed',
                        resp['message'].replaceAll(/\[StreamX]/g,''),
                        'error'
                    )
                  }
                })
              } else {
                updateCluster({
                  id: this.clusterId,
                  clusterName: values.clusterName,
                  address: values.address,
                  description: values.description || null
                }).then((resp)=>{
                  if (resp.data) {
                    this.clusterFormVisible = false
                    this.$swal.fire({
                      icon: 'success',
                      title: values.clusterName.concat(' update successful!'),
                      showConfirmButton: false,
                      timer: 2000
                    })
                    this.handleClusterAll()
                  } else {
                    this.$swal.fire(
                        'Failed',
                        resp['message'].replaceAll(/\[StreamX]/g, ''),
                        'error'
                    )
                  }
                })
              }
            } else {
              if (resp.data === 'exists') {
                this.$swal.fire(
                    'Failed',
                    'the cluster name: ' + values.clusterName + ' is already exists,please check',
                    'error'
                )
              } else if (resp.data === 'fail') {
                this.$swal.fire(
                    'Failed',
                    'the address is invalid or connection failure, please check',
                    'error'
                )
              }
            }
          })
        }
      })
    },

    handleFlinkConf (flink) {
      this.flinkConfVisible = true
      this.versionId = flink.id
      this.flinkName = flink.flinkName
      getFlink({id: this.versionId}).then((resp)=>{
        this.flinkHome = resp.data.flinkHome
        this.flinkConf = resp.data.flinkConf
        this.handleInitEditor()
      })
    },

    handleInitEditor() {
      if (this.editor == null) {
        this.editor = monaco.editor.create(document.querySelector('#conf'), this.getOption())
        this.$nextTick(() => {
          const elem = document.querySelector('#conf')
          this.handleHeight(elem, 210)
        })
      }
      this.$nextTick(()=>{
        this.editor.getModel().setValue(this.flinkConf)
      })
    },

    handleSync () {
      sync({id: this.versionId}).then((resp)=>{
        this.$swal.fire({
          icon: 'success',
          title: this.flinkName.concat(' conf sync successful!'),
          showConfirmButton: false,
          timer: 2000
        })
      })
    },

    handleSetDefault(item) {
      if (item.isDefault) {
        setDefault({ id: item.id }).then((resp)=>{
          this.$swal.fire({
            icon: 'success',
            title: item.flinkName.concat(' set default successful!'),
            showConfirmButton: false,
            timer: 2000
          })
          this.handleFlinkAll()
        })
      }
    },

    handleCloseConf() {
      this.flinkConfVisible = false
    },

    handleHeight(elem, h) {
      const height = document.documentElement.offsetHeight || document.body.offsetHeight
      $(elem).css('height', (height - h) + 'px')
    },

    handleSwitch(setting) {
      update({
        key: setting.key,
        value: setting.value !== 'true'
      }).then((resp) => {
        this.handleSettingAll()
      })
    }
  },

  watch: {
    myTheme() {
      if (this.editor != null) {
        this.editor.updateOptions({
          theme: this.ideTheme()
        })
      }
    }
  },

}
</script>

<style lang="less">
@import "View";
</style>
