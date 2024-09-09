<template>
  <div class="app">
    <div class="container right-panel-active">
      <div class="firstPage">
        <div id="name1"><b>PresentationGen</b></div>
        <div>
          <el-button id="tryButton" type="primary" @click="showModal = true"
            >立即尝试<i class="el-icon-edit-outline"></i
          ></el-button>
          <transition name="modal-slide">
            <modal
              v-if="showModal"
              @close="showModal = false"
              :isVisible="showModal"
              :coverID="1"
              topic="主题"
              sup="补充内容"
              token="your_jwt_token_here"
            />
          </transition>
        </div>
        <div id="name2">
          <h1>告别传统的 PPT 制作方式</h1>
          <h1>让您的每一次演示都轻松高效！</h1>
        </div>
      </div>
      <div class="panel">
        <div class="icon-container">
          <img :src="require('@/assets/fff.jpg')" />
          <div id="flex-mes">
            <div class="panelTitle"><h1>智能化内容生成</h1></div>
            <div class="panelTitle">
              <h2>自动生成文稿，让每一次演讲的准备都变得轻松而高效。</h2>
            </div>
          </div>
        </div>
      </div>
      <div class="panel2">
        <div class="icon-container">
          <div id="flex-mes">
            <div class="panelTitle"><h1>个性化模板选择</h1></div>
            <div class="panelTitle">
              <h2>选择适合您场合的模板，让每一份文稿都独具特色。</h2>
            </div>
          </div>
          <img :src="require('@/assets/g.png')" />
        </div>
      </div>
      <div class="panel">
        <div class="icon-container">
          <img :src="require('@/assets/h.png')" />
          <div id="flex-mes">
            <div class="panelTitle"><h1>直观友好的用户界面</h1></div>
            <div class="panelTitle">
              <h2>高效的功能设计，让复杂的演示制作变得简单。</h2>
            </div>
          </div>
        </div>
      </div>
      <div class="panel2">
        <div class="icon-container">
          <div id="flex-mes">
            <div class="panelTitle"><h1>全面的演讲支持</h1></div>
            <div class="panelTitle">
              <h2>从演示文稿到演讲稿，我们全方位支持您的演讲准备。</h2>
            </div>
          </div>
          <img :src="require('@/assets/i.png')" alt="AI智能生成" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Modal from '@/components/ModalView.vue'
export default {
  name: 'HomeView',
  components: {
    Modal
  },
  mounted () {
    const urlParams = new URLSearchParams(window.location.search)
    this.isRegisterActive = urlParams.get('mode') === 'register'
    this.checkIfVisible() // 初始检查
    window.addEventListener('scroll', this.checkIfVisible)
  },
  beforeDestroy () {
    window.removeEventListener('scroll', this.checkIfVisible)
  },
  methods: {
    goToTest () {
      this.$router.push('/test')
    },
    goBack () {
      console.log('go back')
    },
    checkIfVisible () {
      const elements = document.querySelectorAll('.panel')
      const elements2 = document.querySelectorAll('.panel2')
      const windowHeight = window.innerHeight
      elements.forEach((element) => {
        const positionFromTop = element.getBoundingClientRect().top
        const positionFromBottom = element.getBoundingClientRect().bottom
        console.log(element.id, positionFromTop, positionFromBottom)

        if (positionFromTop - windowHeight <= 0 && positionFromBottom >= 0) {
          // Element is fully in view
          element.classList.add('visible')
          console.log('add visible to', element.id)
        } else {
          // Element is not fully in view
          element.classList.remove('visible')
          console.log('remove visible to', element.id)
        }
      })
      elements2.forEach((element) => {
        const positionFromTop = element.getBoundingClientRect().top
        const positionFromBottom = element.getBoundingClientRect().bottom

        if (positionFromTop - windowHeight <= 0 && positionFromBottom >= 0) {
          // Element is fully in view
          element.classList.add('visible')
        } else {
          // Element is not fully in view
          element.classList.remove('visible')
        }
      })
    }
  },
  data () {
    return {
      showModal: false,
      isRegisterActive: false,
      username: '',
      password: '',
      phone_number: '',
      otp: ''
    }
  }
}
</script>

  <style scoped>
/* CSS 内容 */
[v-cloak] {
  display: none;
}
#app {
  width: 100%;
  height: 100%;
  overflow-x: hidden;
  /* border:3px dashed #475669; */
}
.firstPage {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 50px;
}

#firstButton {
  font-size: 20px;
}
#tryButton {
  font-size: 30px;
  padding: 32px 80px;
  margin-bottom: 100px;
  border-radius: 50px;
}
#name1 {
  margin: 0 auto;
  margin-bottom: 50px;
  padding: 50px;
  font-size: 120px;
  font-family: "Times New Roman", Times, serif;
  background-image: linear-gradient(-90deg, #4fa5fa, rgb(22, 90, 225));
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}
#name2 {
  margin: auto;
  margin-bottom: 150px;
  padding: 20px;
  background-image: linear-gradient(-90deg, #4fa5fa, rgb(22, 90, 225));
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  /* border:3px dashed #475669; */
}

.container {
  width: 100%;
  background-image: radial-gradient(
    circle,
    #ffffff84,
    rgb(255, 255, 255),
    #4fa5fa
  );
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
  overflow-x: hidden;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.panel {
  flex: 1;
  background-color: #dbeafe;
  border-radius: 0 70px 70px 0;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  margin: 150px;
  padding: 100px;
  margin: 100px;
  padding: 100px;
  box-shadow: 7px 0 7px 0 rgba(87, 87, 87, 0.2);
  opacity: 0;
  transform: translateX(-100%);
  transition: all 0.8s ease-in-out;
  will-change: opacity;
}

.panel.visible {
  opacity: 1;
  transform: translateX(0);
  animation: yang1 1s;
}

@keyframes yang1 {
  from {
    transform: translateX(-100%);
  }
  to {
    transform: translateX(0px);
  }
}

.panel2 {
  flex: 1;
  background-color: #dbeafe;
  border-radius: 70px 0 0 70px;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  padding: 100px;
  margin: 150px;
  box-shadow: 0 7px 7px 0 rgba(87, 87, 87, 0.2);
  opacity: 0;
  transform: translateX(100%);
  transition: all 0.8s ease-in-out;
}
.panel2.visible {
  opacity: 1;
  transform: translateX(0);
  animation: yang2 1s;
}

@keyframes yang2 {
  from {
    transform: translateX(100%);
  }
  to {
    transform: translateX(0px);
  }
}

.icon-container {
  display: flex;
  flex-direction: row;
  align-items: center;
  border-radius: 10px;
  align-items: center;
}

.icon-container img {
  width: 300px;
  height: 300px;
  border-radius: 10px;
  margin-right: 80px;
}

.panelTitle {
  background-image: linear-gradient(-90deg, #4fa5fa, rgb(1, 100, 239));
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  display: block;
  margin-bottom: 50px;
}

.flex-mes {
  padding: 100px;
}
.modal-slide-enter-active,
.modal-slide-leave-active {
  transition: transform 0.3s ease; /* 添加一个过渡效果，使动画更平滑 */
}

.modal-slide-enter,
.modal-slide-leave-to {
  transform: translateY(100%); /* 设置初始位置和结束位置 */
}
</style>
