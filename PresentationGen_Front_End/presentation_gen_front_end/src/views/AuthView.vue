<template>
  <div class="center">
    <div class="logon">
      <div :class="overlaylong">
        <div class="overlaylong-Signin" v-if="disfiex == 0" >
          <h2 class="overlaylongH2">账号密码登录注册</h2>
          <el-input type="text" placeholder="用户名或手机号" v-model="username" />
          <el-input placeholder="密码" v-model="password" show-password/>
          <div class="container" style="display: flex">
            <button class="registerButton" @click="register">注册</button>
            <button class="submitButton" @click="login">登录</button>
          </div>
        </div>
        <div class="overlaylong-Signup" v-if="disfiex == 1">
          <h2 class="overlaylongH2">手机号注册登录</h2>
          <input
            type="text"
            placeholder="手机号(未注册的手机号将自动注册)"
            v-model="phoneNumber"
          />
          <div class="otp-container">
            <input type="text" placeholder="验证码" class="OTP" v-model="otp" />
            <button
              class="sendOTPBotton"
              :disabled="disableSendButton"
              @click="sendOTP"
            >
              <template v-if="countdown > 0"> {{ countdown }}s </template>
              <template v-else> 发送验证码 </template>
            </button>
          </div>
          <button class="submitButton" @click="verifyOTP">提交</button>
        </div>
      </div>
      <div :class="overlaytitle">
        <div class="overlaytitle-Signin" v-if="disfiex == 0">
          <h2 class="overlaytitleH2">欢迎回来!</h2>
          <p class="overlaytitleP">开始PPT生成之旅</p>
          <button class="buttongohs" @click="SwitchOTP">手机验证码登录</button>
        </div>
        <div class="overlaytitle-Signup" v-if="disfiex == 1">
          <h2 class="overlaytitleH2">欢迎回来!</h2>
          <p class="overlaytitleP">开始PPT生成之旅</p>
          <button class="buttongohs" @click="SwitchPassword">账号密码登录</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import CryptoJS from 'crypto-js'
export default {
  data () {
    return {
      overlaylong: 'overlaylong',
      overlaytitle: 'overlaytitle',
      disfiex: 0,
      countdown: 0, // 添加倒计时变量
      disableSendButton: false, // 控制发送按钮禁用状态
      username: '', // 用户名
      password: '', // 密码
      phoneNumber: '', // 手机号
      otp: '', // 验证码
      otpToken: ''
    }
  },
  methods: {
    SwitchOTP () {
      this.overlaylong = 'overlaylongleft'
      this.overlaytitle = 'overlaytitleright'
      setTimeout(() => {
        this.disfiex = 1
      }, 200)
    },
    SwitchPassword () {
      this.overlaylong = 'overlaylongright'
      this.overlaytitle = 'overlaytitleleft'

      setTimeout(() => {
        this.disfiex = 0
      }, 200)
    },
    login () {
      if (this.password.length < 6 || this.password.length > 20) {
        this.$message.warning('密码长度在6个字符到20个字符之间')
        return
      }
      const hashedPassword = CryptoJS.SHA256(this.password).toString()
      this.$axios
        .post('/auth/login', {
          username: this.username,
          password: hashedPassword
        })
        .then((response) => {
          if (response.data.code === 1) {
            const token = response.data.data.token
            localStorage.setItem('token', token) // Save token
            this.$router.push({ name: 'Home' }) // Redirect to /person
            location.reload()
            this.$message.success('登录成功') // Popup reminder
          } else {
            this.$message.error('登录失败: ' + response.data.msg) // Popup reminder
            this.username = '' // Clear input
            this.password = ''
          }
        })
        .catch((error) => {
          this.$message.error('登录异常: ' + error.message) // Popup display exception information
          this.username = '' // Clear input
          this.password = ''
        })
    },
    register () {
      if (this.password.length < 6 || this.password.length > 20) {
        this.$message.error('密码长度有误')
        return
      }
      const hashedPassword = CryptoJS.SHA256(this.password).toString()
      this.$axios
        .post('/auth/register', {
          username: this.username,
          password: hashedPassword
        })
        .then((response) => {
          if (response.data.code === 1) {
            this.$message.success('注册成功，请登录')
            this.password = ''
          } else {
            this.$message.error('注册失败: ' + response.data.msg) // Popup reminder
            this.username = '' // Clear input field
            this.password = ''
          }
        })
        .catch((error) => {
          this.$message.error('注册异常: ' + error.message) // Popup display exception information
          this.username = '' // Clear input field
          this.password = ''
        })
    },
    sendOTP () {
      // 禁用登录按钮
      this.disableSendButton = true
      this.$axios
        .post('/auth/send-login-otp', {
          phone_number: this.phoneNumber
        })
        .then((response) => {
          this.otpToken = response.data.data.otpToken
          console.log('OTP sent successfully', response)
          // 开始一分钟倒计时
          let seconds = 60
          this.countdown = seconds
          const interval = setInterval(() => {
            seconds--
            this.countdown = seconds
            if (seconds <= 0) {
              clearInterval(interval)
              this.countdown = 0
              // 启用发送按钮
              this.disableSendButton = false
            }
          }, 1000)
        })
        .catch((error) => {
          console.error('Failed to send OTP', error)
          this.disableSendButton = false
        })
    },
    verifyOTP () {
      this.$axios
        .post(
          '/auth/verify-login-otp',
          {
            otp: this.otp
          },
          {
            headers: {
              otpToken: this.otpToken
            }
          }
        )
        .then((response) => {
          if (response.data.code === 1) {
            localStorage.setItem('token', response.data.data.token)
            this.$router.push({ name: 'Home' }) // 跳转到/home
            location.reload()
          } else {
            alert('验证码验证失败: ' + response.data.msg) // 弹窗提醒失败原因
            this.otp = '' // 清空验证码输入框
          }
          this.otpToken = ''
        })
        .catch((error) => {
          alert('验证码验证异常: ' + error.message) // 弹窗显示异常信息
          this.otp = ''
        })
    }
  }
}
</script>
<style>
html,
body {
  height: 100%;
  margin: 0;
}

.center {
  width: 100%;
  height: 100%;
  background-size: cover; /* 或者使用 contain，视情况而定 */
  background-repeat: no-repeat;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  background-image: radial-gradient(circle, #ffffff, rgb(255, 255, 255), #4fa4fa74) ;
}

.logon {
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 14px 28px rgba(0, 0, 0, 0.25), 0 10px 10px rgba(0, 0, 0, 0.22);
  /* position: relative;
  overflow: hidden; */
  width: 768px;
  max-width: 100%;
  min-width: 720px;
  min-height: 480px;
  margin-top: 20px;
  display: flex;
  background: -webkit-linear-gradient(right, #4284db, #30deb5);
}

.overlaylong {
  border-radius: 10px 0 0 10px;
  width: 50%;
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.overlaylongleft {
  border-radius: 0px 10px 10px 0px;
  width: 50%;
  background-color: #fff;
  transform: translateX(100%);
  transition: transform 0.6s ease-in-out;
  display: flex;
  align-items: center;
  justify-content: center;
}

.overlaylongright {
  border-radius: 10px 0 0 10px;
  width: 50%;
  background-color: #fff;
  transform: translateX(0%);
  transition: transform 0.6s ease-in-out;
  display: flex;
  align-items: center;
  justify-content: center;
}

.overlaytitle {
  border-radius: 0px 10px 10px 0px;
  width: 50%;
  background-color: rgba(0, 0, 0, 0);
  display: flex;
  align-items: center;
  justify-content: center;
}

.overlaytitleH2 {
  font-size: 30px;
  color: #fff;
  margin-top: 20px;
}

.overlaytitleP {
  font-size: 20px;
  color: #fff;
  font-weight: bold;
  margin-top: 20px;
}

.overlaytitleleft {
  border-radius: 0px 10px 10px 0px;
  width: 50%;
  background-color: rgba(0, 0, 0, 0);
  display: flex;
  align-items: center;
  justify-content: center;
  transform: translateX(0%);
  transition: transform 0.6s ease-in-out;
}

.overlaytitleright {
  border-radius: 0px 10px 10px 0px;
  width: 50%;
  background-color: rgba(0, 0, 0, 0);
  display: flex;
  align-items: center;
  justify-content: center;
  transform: translateX(-100%);
  transition: transform 0.6s ease-in-out;
}

.overlaytitle-Signin {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}

.overlaytitle-Signup {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}

.buttongohs {
  width: 180px;
  height: 40px;
  border-radius: 50px;
  border: none;
  color: #ffffff;
  font-size: 15px;
  text-align: center;
  line-height: 40px;
  margin-top: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.overlaylongH2 {
  font-size: 25px;
  color: black;
  /* width: 250px; */
}

.overlaylong-Signin {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  width: 70%;
}

.overlaylong-Signup {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}

input {
  background-color: #eee;
  border: none;
  padding: 12px 15px;
  margin: 10px 0;
  width: 240px;
}

.otp-container {
  display: flex;
  align-items: center; /* 垂直居中 */
}

.OTP {
  background-color: #eee;
  border: none;
  padding: 12px 15px;
  margin: 10px 0;
  width: 120px;
}

.sendOTPBotton {
  background-color: rgba(255, 255, 255, 0);
  color: #29c0ea;
  font-weight: bold;
  font-size: small;
  margin: 10px 0;
  width: 120px;
  border: #29eac4;
}

h3 {
  font-size: 10px;
  margin-top: 10px;
  cursor: pointer;
}
.registerButton {
  background-color: #4284db;
  border: none;
  width: 100px;
  height: 40px;
  border-radius: 50px;
  font-size: 15px;
  color: #fff;
  line-height: 40px;
  margin-top: 30px;
  margin-right: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.submitButton {
  background-color: #29eac4;
  border: none;
  width: 180px;
  height: 40px;
  border-radius: 50px;
  font-size: 15px;
  color: #fff;
  text-align: center;
  line-height: 40px;
  margin-top: 30px;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
