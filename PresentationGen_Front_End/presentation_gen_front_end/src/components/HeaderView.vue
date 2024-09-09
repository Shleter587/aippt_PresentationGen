<template>
  <el-menu
    :default-active="activeIndex"
    class="el-menu-vertical-demo"
    mode="horizontal"
    text-color="#89CFF0"
    active-text-color="#4fa5fa"
    style="display: flex"
    @select="handleSelect"
    position:fixed
    z-index:100000
  >
    <el-menu-item index="0" style="background-color: #8bc5fe"
      ><el-image style="width: 140px; height: 58px" :src="logoUrl"
    /></el-menu-item>
    <el-menu-item index="1">首页</el-menu-item>
    <el-menu-item index="2">我的文档</el-menu-item>
    <el-menu-item index="3">PPT模板库</el-menu-item>
    <el-menu-item index="5">关于我们</el-menu-item>
    <el-menu-item v-if="!isLoggedIn" index="4" style="margin-left: auto"
      >登录/注册</el-menu-item>
    <el-menu-item v-else index="4" style="margin-left: auto">
      <el-avatar :size="30" :src="circleUrl"></el-avatar
    >
      {{ userName }}
    </el-menu-item>
  </el-menu>
</template>

<script>
export default {
  data () {
    return {
      circleUrl:
        'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      logoUrl: require('@/assets/MenuLogo.png'),
      activeIndex: '1',
      activeIndex2: '1',
      isLoggedIn: false, // 初始登录状态
      idImage: '',
      userName: ''
    }
  },
  methods: {
    handleSelect (key) {
      if (key === '4') {
        if (this.isLoggedIn) {
          this.$router.push({ name: 'Person' }).catch((err) => {
            console.log(err)
          })
        } else {
          this.$router.push({ name: 'Auth' }).catch((err) => {
            console.log(err)
          })
        }
      } else if (key === '1' || key === '0') {
        this.$router.push({ name: 'Home' }).catch((err) => {
          console.log(err)
        })
      } else if (key === '2') {
        if (this.isLoggedIn) {
          this.$router.push({ name: 'History' }).catch((err) => {
            console.log(err)
          })
        } else {
          this.$router.push({ name: 'Auth' }).catch((err) => {
            console.log(err)
          })
          this.$message.info('请先登录')
        }
      } else if (key === '3') {
        this.$router.push({ name: 'PPT' }).catch((err) => {
          console.log(err)
        })
      } else if (key === '5') {
        this.$router.push({ name: 'Help' }).catch((err) => {
          console.log(err)
        })
      }
    },
    checkLoginStatus () {
      this.$axios
        .post('/auth/check', null, {
          headers: {
            token: localStorage.getItem('token')
          }
        })
        .then((response) => {
          this.isLoggedIn = response.data.code
          console.log(response.data)
          if (this.isLoggedIn === 1) {
            this.getUserName()
          }
        })
    },
    getUserName () {
      this.$axios
        .post('/auth/info', null, {
          headers: {
            token: localStorage.getItem('token')
          }
        })
        .then((response) => {
          this.userName = response.data.data.username
          this.circleUrl = response.data.data.profilePictureAddr
          console.log(response.data)
        })
    }
  },
  mounted () {
    this.checkLoginStatus()
  }
}
</script>

<style>
.el-menu-vertical-demo {
  background-color: rgb(22, 90, 225);
  /* background-image: linear-gradient(-90deg, #4fa5fa, rgb(22, 90, 225)); */
}
#subMenu {
  display: flex;
  justify-content: center;
}
</style>
