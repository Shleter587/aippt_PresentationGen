<template>
  <div>
    <div class="markdown" v-html="receivedText"></div>
    <!-- 渲染Markdown转换后的HTML -->
  </div>
</template>

<script>
import { marked } from 'marked'

export default {
  props: {
    topic: String,
    sup: String,
    token: String,
    send: Boolean
  },
  data () {
    return {
      receivedText: '',
      originText: ''
    }
  },
  watch: {
    // 监听prop变化，重新开始流数据获取
    send (newVal, oldVal) {
      if (newVal === true && oldVal !== newVal) {
        this.receivedText = '' // 清空旧数据
        this.fetchStreamData() // 重新获取数据
      }
    }
  },
  methods: {
    fetchStreamData () {
      let url = '/api/send'
      if (process.env.NODE_ENV === 'development') {
        url = 'http://localhost:8080/api/send'
      }
      const headers = new Headers({
        'Content-Type': 'application/json',
        token: this.token
      })
      const requestData = {
        method: 'POST',
        headers: headers,
        body: JSON.stringify({
          topic: this.topic,
          sup: this.sup
        })
      }

      fetch(url, requestData)
        .then(response => response.body.getReader())
        .then(reader => {
          const readStream = new ReadableStream({
            start: controller => {
              const push = () => {
                reader.read().then(({ done, value }) => {
                  if (done) {
                    controller.close()
                    return
                  }
                  const textDecoder = new TextDecoder()
                  const chunk = textDecoder.decode(value)
                  this.originText += chunk
                  this.originText = this.originText.replace(/- !\([^)]*\)!/g, '')
                  const streamIDRegex = /streamID:(.{38})/
                  const match = this.originText.match(streamIDRegex)
                  if (match) {
                    this.$emit('streamIDReceived', match[1].trim())
                    this.originText = this.originText.replace(streamIDRegex, '')
                  }
                  this.receivedText = marked(this.originText)
                  controller.enqueue(value)
                  push()
                }).catch(err => {
                  console.error('Read error:', err)
                })
                this.$emit('scroll')
              }
              push()
            }
          })
          return new Response(readStream)
        })
        .catch(err => {
          console.error('Stream failed:', err)
        })
    }
  }
}
</script>

<style>
/* 标题的字体大小设置 */
.markdown{
    height: 100%;
}
h1 {
  font-size: 24px; /* 最顶级标题，最大 */
  color: #333;
  font-family: "微软雅黑";
  margin-top: 20px;
}

h2 {
  font-size: 20px; /* 第二级标题 */
  color: #333;
  font-family: "微软雅黑";
  margin-top: 16px;
}

h3 {
  font-size: 16px; /* 第三级标题 */
  color: #333;
  font-family: "微软雅黑";
  margin-top: 12px;
}

li {
  font-size: 16px; /* 第四级标题 */
  color: #333;
  font-family: "微软雅黑";
  margin-top: 12px;
}
</style>
