<template>
  <div v-if="isVisible" class="modal-overlay" @click.self="closeModal">
    <el-container>
      <el-main>
        <el-row>
          <span
            style="
              color: black;
              font-weight: bold;
              font-size: larger;
              margin-top: 2px;
            "
            >大纲-AI生成:</span
          >
          <el-divider />
        </el-row>
        <el-row>
          <el-col :span="4">
            <el-row>
              <span style="color: gray">演示标题:</span>
            </el-row>
            <el-row>
              <el-input
                v-model="topicInput"
                maxlength="20"
                placeholder="键入你的演示标题"
                show-word-limit
              ></el-input>
            </el-row>
            <el-row style="height: 27px">
              <span style="color: gray">补充信息:(选填)</span>
            </el-row>
            <el-row>
              <el-input
                id="supp"
                type="textarea"
                placeholder="键入你的补充信息"
                :rows="13"
                v-loading="send && !streamCompleted"
                element-loading-background="transparent"
                v-model="supInput"
                maxlength="1000"
                show-word-limit
              >
              </el-input>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-button
                  type="primary"
                  @click="startStreaming"
                  :disabled="send"
                  style="margin-top: 15px"
                >
                  生成大纲
                </el-button>
              </el-col>
              <el-col :span="10" offset=2>
                <el-button
                  type="primary"
                  :disabled="send && !streamCompleted"
                  @click="editPPT"
                  style="margin-top: 15px"
                >
                  编辑PPT
                </el-button>
              </el-col>
            </el-row>
          </el-col>
          <el-col :span="1">
            <el-row>
              <el-divider direction="vertical" />
            </el-row>
          </el-col>
          <el-col :span="19">
            <el-card class="stream-display-area" shadow="hover">
              <StreamDisplay
                @streamIDReceived="getStreamID"
                @scroll="scrollToBottom"
                :topic="topicInput"
                :sup="supInput"
                :token="token"
                :send="send"
              />
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import StreamDisplay from '@/components/StreamDisplay.vue'

export default {
  components: {
    StreamDisplay
  },
  props: {
    isVisible: Boolean,
    coverID: Number
  },
  mounted () {
    this.token = localStorage.getItem('token')
    if (this.token === null) {
      this.$router.push({ name: 'Auth' })
      this.$message.info('请先登录')
    } else {
      this.$axios
        .post('/auth/check', null, {
          headers: {
            token: this.token,
            'Content-Type': 'application/json'
          }
        })
        .then((response) => {
          if (response.data.code === 0) {
            this.$router.push({ name: 'Auth' })
          }
        })
    }
    console.log(this.token)
  },
  data () {
    return {
      topicInput: '',
      supInput: '',
      streamID: '',
      token: '',
      send: false,
      streamCompleted: false
    }
  },
  methods: {
    startStreaming () {
      this.send = true
      this.streamCompleted = false
    },
    getStreamID (data) {
      this.streamID = data
      console.log('获取到了streamID' + this.streamID)
      this.streamCompleted = true
      this.send = false
    },
    closeModal () {
      this.$emit('close')
    },
    editPPT () {
      const data = JSON.stringify({
        streamID: this.streamID,
        templates: [this.coverID, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1]
      })
      this.$axios
        .post('/init-ppt', data, {
          headers: {
            token: this.token,
            'Content-Type': 'application/json'
          }
        })
        .then((response) => {
          console.log(response.data.data.markdownData)
          this.$router.push({
            name: 'Edit',
            params: {
              markdownData: JSON.stringify(response.data.data.markdownData),
              templates: JSON.stringify([
                this.coverID, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
              ]),
              id: JSON.stringify(response.data.data.id)
            }
          })
        })
        .catch((error) => {
          console.error(error)
        })
    },
    scrollToBottom () {
      this.$nextTick(() => {
        const element = this.$el.querySelector('.stream-display-area')
        element.scrollTop = element.scrollHeight
      })
    }
  }
}
</script>

  <style >
  .el-divider--horizontal{
    margin-top: 20px!important;
  }
button:disabled {
  background-color: #ccc;
}
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(252, 252, 252, 0.5);
  backdrop-filter: blur(8px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000; /* 可能需要调整，确保 Header 在其他内容上方显示 */
}

.el-container {
  background-color: #ffffff;
  width: 100%;
  height: 600px;
  bottom: 0px;
  position: absolute;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  width: 80%;
  height: 70%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

input,
textarea {
  width: 90%;
  padding: 10px;
  margin-bottom: 10px;
  border-radius: 5px;
  border: 1px solid #ccc;
}

.stream-display-area {
  background-color: rgb(245, 239, 239);
  height: 430px;
  max-height: 100%;
  width: 94%;
  margin-left: 3%;
  margin-right: 3%;
  overflow-y: auto;
}

.el-divider--vertical {
  display: inline-block;
  width: 1px;
  height: 400px;
  margin: 50% 50%;
  vertical-align: middle;
  position: relative;
}

button {
  margin: 10px;
  border: none;
  background-color: #007aff;
  color: white;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
}
button:focus {
  outline: none;
}

#supp {
  margin-top: 10px;
}

/* 设定md渲染风格 */
/* 针对特定元素的子元素设置样式 */
.markdown {
  height: 100%;
}

.markdown h1 {
  font-size: 28px; /* 最顶级标题，最大 */
  color: #333;
  margin-top: 24px;
}

.markdown h2 {
  font-size: 24px; /* 第二级标题 */
  color: #333;
  margin-top: 20px;
}

.markdown h3 {
  font-size: 20px; /* 第三级标题 */
  color: #333;
  margin-top: 16px;
}

.markdown h4 {
  font-size: 18px; /* 第四级标题 */
  color: #333;
  margin-top: 14px;
}
</style>
