<template>
  <div style="height: 100%; width: 100%; overflow: hidden;">
      <el-col :span="16" :offset="4" style="margin-top: 20px">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>个人中心</span>
          </div>
          <div class="avatar">
            <div>
              <el-avatar :src="userInfo.profilePictureAddr" :size="120"></el-avatar>
            </div>
            <div>
              <input type="file" @change="handleFileUpload" style="display: none" ref="fileInput" />
              <el-button type="primary" plain @click="triggerFileSelect"
                style="justify-content: center; margin-top: 10px;margin-bottom: 10px;">修改头像</el-button>
            </div>
          </div>
          <el-divider></el-divider>
          <div class="personal-relation">
            <div class="relation-item">用户名:{{ userInfo.username }}</div>
          </div>
          <div class="personal-relation">
            <div class="relation-item">
              手机号:{{ userInfo.phoneNumber }}
              <el-button id="changeButton" type="primary" plain @click="triggerChangePhoneNumber">修改手机号</el-button>
            </div>
          </div>
          <div class="personal-relation">
            <div class="relation-item">
              创建时间:&nbsp;{{ userInfo.createTime }}
            </div>
          </div>
          <div class="personal-relation">
            <div class="relation-item">
              <el-button type="danger" plain @click="logoutDialogVisible = true">退出登录</el-button>
              <el-button id="changeButton" type="primary" plain @click="triggerPasswordDialog">修改密码</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    <el-dialog title="退出登录" :visible.sync="logoutDialogVisible" width="30%" :before-close="handleClose">
      <span>确认退出当前账号吗？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="logoutDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="logout">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog title="修改手机号" :visible.sync="resetPhoneDialogVisible" width="30%" @close="resetDialog">
      <el-input v-model="new_phone" placeholder="请输入手机号"></el-input>
      <span slot="footer" class="dialog-footer">
        <el-button @click="resetPhoneDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updatePhoneNumber">确认</el-button>
      </span>
    </el-dialog>
    <el-dialog :close-on-click-modal="false" title="重置密码" :visible.sync="showPasswordDialog"
      @close="resetPasswordDialog" width="30%">
      <p>我们将发送一个验证码到您的手机号: {{ userInfo.phoneNumber }}</p>
      <el-form @submit.prevent="submitNewPassword">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-input v-model="otp" placeholder="请输入验证码"></el-input>
          </el-col>
          <el-col :span="12" style="display: flex;align-items: center;">
            <button class="otpBotton" :disabled="disableSendButton" @click="sendVerificationCode">
              <template v-if="countdown > 0"> {{ countdown }}s </template>
              <template v-else> 发送验证码 </template>
            </button>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="20">
            <el-form-item label="新密码">
              <el-input type="password" v-model="new_password" placeholder="请输入新密码" show-password></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item>
          <el-button type="primary" @click="submitNewPassword">确认</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import CryptoJS from 'crypto-js'
export default {
  data () {
    return {
      userInfo: {
        username: '',
        phoneNumber: '',
        createTime: '',
        profilePictureAddr: '',
        password: ''
      },
      token: '',
      logoutDialogVisible: false,
      resetPhoneDialogVisible: false,
      showPasswordDialog: false,
      photo: null,
      new_phone: '',
      otp: '',
      new_password: '',
      otpToken: '',
      countdown: 0, // 添加倒计时变量
      disableSendButton: false // 控制发送按钮禁用状态
    }
  },
  mounted () {
    this.fetchUserInfo()
  },
  methods: {
    handleFileUpload (event) {
      const selectedFile = event.target.files[0]
      if (selectedFile && selectedFile.size <= 2097152) {
        // 检查文件大小（2MB）
        this.photo = selectedFile
        if (!this.photo) {
          this.$message.error('请选择一个有效的文件！')
          return
        }
        const formData = new FormData()
        formData.append('photo', this.photo)
        this.$axios
          .post('/auth/upload-profile-picture', formData, {
            headers: {
              token: localStorage.getItem('token'),
              'Content-Type': 'multipart/form-data'
            }
          })
          .then((response) => {
            console.log('"Complete Response: "', response)
            console.log('"Response Data: "', response.data)
            this.profilePictureAddr = response.data.data.picture_url
            this.$message.success('上传成功!')
            location.reload()
          })
          .catch((error) => {
            this.$message.error('上传失败:', error)
          })
      } else {
        this.$message.error('文件必须小于2MB!')
      }
    },
    triggerFileSelect () {
      this.$refs.fileInput.click() // 触发文件选择
    },
    fetchUserInfo () {
      this.$axios
        .post('/auth/info', null, {
          headers: {
            token: localStorage.getItem('token'),
            'Content-Type': 'application/json'
          }
        })
        .then((response) => {
          console.log('data received:', response.data)
          this.userInfo = {
            username: response.data.data.username,
            phoneNumber: response.data.data.phoneNumber,
            createTime: response.data.data.createTime,
            profilePictureAddr: response.data.data.profilePictureAddr
          }
          console.log('Updated userInfo:', this.userInfo)
        })
        .catch((error) => {
          console.error('Error fetching user info:', error)
        })
    },
    updatePhoneNumber () {
      const reg = /^[1][3,4,5,7,8][0-9]{9}$/
      if (!reg.test(this.new_phone)) {
        this.$message({ message: '请输入正确的手机号', type: 'error' })
        return
      }
      this.$axios
        .post(
          '/auth/set-phone',
          { new_phone: this.new_phone },
          {
            headers: {
              token: localStorage.getItem('token'),
              'Content-Type': 'application/json'
            }
          }
        )
        .then((response) => {
          console.log('data received:', response.data)
          if (response.data.code === 1) {
            this.userInfo.phoneNumber = this.new_phone
            this.resetPhoneDialogVisible = false
            this.$message({ message: '手机号更新成功！', type: 'success' })
          } else {
            this.$message({
              message: '更新失败：' + response.data.msg,
              type: 'error'
            })
          }
        })
        .catch((error) => {
          console.error('更新手机号失败', error)
          this.$message({
            message: '更新失败：' + error.message,
            type: 'error'
          })
        })
    },
    triggerChangePhoneNumber () {
      this.resetPhoneDialogVisible = true
    },
    logout () {
      localStorage.removeItem('token')
      this.$router.push({ name: 'Auth' }).catch(() => { })
      location.reload()
      this.$message.success('登出成功')
    },
    resetDialog () {
      this.new_phone = '' // 重置输入框
    },
    triggerPasswordDialog () {
      if (this.userInfo.phoneNumber) {
        this.showPasswordDialog = true // 如果有手机号，显示弹窗
      } else {
        this.$message.warning('请先绑定手机号') // 如果没有手机号，提示绑定
      }
    },
    sendVerificationCode () {
      const token = localStorage.getItem('token')
      if (!token) {
        this.$message.error('请先登录！')
        return
      } else this.disableSendButton = true
      this.$axios
        .post('/auth/send-reset-otp', null, {
          headers: {
            token: localStorage.getItem('token')
          }
        })
        .then((response) => {
          if (response.data.code === 1) {
            this.$message.success('验证码已发送到您的手机')
            this.otpToken = response.data.data.otpToken
          } else {
            this.$message.error('发送失败：' + response.data.msg)
          }
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
          console.error('请求失败', error)
          this.$message.error('发送失败，请稍后再试')
        })
    },
    resetPasswordDialog () {
      this.otp = ''
      this.newPassword = ''
      this.showPasswordDialog = false
    },
    submitNewPassword () {
      if (this.otp.length !== 4) {
        this.$message.warning('请填写有效的验证码和新密码！')
        return
      }
      if (this.new_password.length < 6 || this.new_password.length > 20) {
        this.$message.warning('密码长度在6个字符到20个字符之间')
        return
      }
      const hashedPassword = CryptoJS.SHA256(this.new_password).toString()
      const data = {
        new_password: hashedPassword,
        otp: this.otp
      }
      this.$axios
        .post('/auth/verify-reset-pwd', data, {
          headers: {
            token: localStorage.getItem('token'),
            'Content-Type': 'application/json',
            otpToken: this.otpToken
          }
        })
        .then((response) => {
          if (response.data.code === 1) {
            console.log('Data:', response.data)
            this.$message.success('密码已重置成功，请重新登陆')
            localStorage.removeItem('token')
            this.$router.push({ name: 'Auth' })
          } else {
            this.$message.error('错误: ' + response.data.msg)
          }
        })
        .catch((error) => {
          console.error('密码重置失败:', error)
          this.$message.error('密码重置失败，请重试')
        })
    }
  }
}
</script>

<style scoped>
.text {
  font-size: 20px;
}

.item {
  margin-bottom: 18px;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}

.clearfix:after {
  clear: both;
}

.box-card {
  width: 100%;
  height: 80%;
  font-size: 20px;
  padding: 0px;
}
.name-role {
  font-size: 20px;
  padding: 5px;
  text-align: center;
}

.avatar {
  display: flex;
  flex-direction: column;
  text-align: center;
}

.sender {
  text-align: center;
}

.register-info {
  text-align: center;
  padding-top: 10px;
}

.personal-relation {
  font-size: 18px;
  padding: 0px 5px 15px;
  margin-right: 1px;
  width: 100%;
}

.relation-item {
  padding: 18px;
}

.dialog-footer {
  padding-top: 10px;
  padding-left: 10%;
}

.el-row {
  margin-bottom: 20px;
}

.el-col {
  border-radius: 4px;
}

.bg-purple-dark {
  background: #99a9bf;
}

.bg-purple {
  background: #d3dce6;
}

.bg-purple-light {
  background: #e5e9f2;
}

.grid-content {
  border-radius: 4px;
  min-height: 36px;
}

.row-bg {
  padding: 10px 0;
  background-color: #f9fafc;
}

#changeButton {
  float: right;
  padding: 10px;
}
</style>
