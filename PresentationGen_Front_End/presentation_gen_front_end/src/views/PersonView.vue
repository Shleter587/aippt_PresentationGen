<template>
    <el-container style=" height: 90vh; display: flex; align-items: center;top:60px;overflow-y: hidden;">
     <el-aside width="200px">
                <el-menu
                default-active="3"
                class="el-menu-vertical-demo1"
                @open="handleOpen"
                @close="handleClose"
                text-color="#4fa5fa"
                active-text-color="#4fa5fa"
                style="border: none;"
                >
              <el-menu-item index="2" @click="showComponent('HistoryView')">
                <i class="el-icon-document"></i>
                <span slot="title">历史记录</span>
              </el-menu-item>
              <el-menu-item index="3" @click="showComponent('UserView')">
                  <i class="el-icon-setting"></i>
                  <span slot="title">账户信息</span>
              </el-menu-item>
            </el-menu>
    </el-aside>
    <el-main style="height: 100%;">
        <component :is="currentView"></component>
    </el-main>
</el-container>
  </template>
<script>
import UserView from './UserView.vue'
import HistoryView from './HistoryView.vue'
import HeaderView from '@/components/HeaderView.vue'
export default {
  name: 'PersonView',
  components: {
    UserView,
    HistoryView,
    HeaderView
  },
  data () {
    return {
      showModal: false,
      currentView: 'UserView'
    }
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
    },
    showComponent (componentName) {
      this.currentView = componentName// 设置当前要显示的组件名
    }
  }
}
</script>

<style scoped>

.el-menu-vertical-demo1 {
  display: flex;
  flex-direction: column;
  justify-content: center;
  height: 500px;
  padding: 30px;
  width: 140px;
  background-image: linear-gradient(white,#a2c6e4,#c4d8ec,white)
}

.el-card__body, .el-main{
  padding: 5px;
}
.View {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
</style>
