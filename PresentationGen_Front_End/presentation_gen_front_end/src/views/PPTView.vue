<!-- eslint-disable vue/no-unused-vars -->
<!-- eslint-disable vue/require-v-for-key -->
<template>
  <div class="container">
    <div id="title">
      <h1>你想要做的演讲是什么？</h1>
      <h2>海量设计模版，覆盖多种设计场景，丰富的模版素材库供你挑选</h2>
      <h2 style="margin-bottom: 20px; color: cornflowerblue">
        点击模板开始创建你的演示
      </h2>
    </div>
    <div class="radioGroup">
      颜色&nbsp;:&nbsp;
      <el-radio-group v-model="selectedColor" @change="handleChange">
        <el-radio-button label="全部"></el-radio-button>
        <el-radio-button label="红色"></el-radio-button>
        <el-radio-button label="橙色"></el-radio-button>
        <el-radio-button label="黄色"></el-radio-button>
        <el-radio-button label="绿色"></el-radio-button>
        <el-radio-button label="蓝色"></el-radio-button>
        <el-radio-button label="粉色"></el-radio-button>
        <el-radio-button label="紫色"></el-radio-button>
        <el-radio-button label="黑色"></el-radio-button>
        <el-radio-button label="白色"></el-radio-button>
      </el-radio-group>
    </div>
    <div class="radioGroup">
      风格&nbsp;:&nbsp;
      <el-radio-group v-model="selectedStyle" @change="handleChange">
        <el-radio-button label="全部"></el-radio-button>
        <el-radio-button label="插画"></el-radio-button>
        <el-radio-button label="简约"></el-radio-button>
        <el-radio-button label="科技"></el-radio-button>
        <el-radio-button label="可爱"></el-radio-button>
        <el-radio-button label="国风"></el-radio-button>
        <el-radio-button label="风景"></el-radio-button>
      </el-radio-group>
    </div>
    <div class="grid-container">
      <div class="grid-item" v-for="emp in emps" :key="emp.id">
        <img v-bind:src="emp.image" alt="Image"  @click="showModal = true;selectID=emp.id;" />
        <p class="image-caption">{{ emp.templateName }}</p>
      </div>
    </div>
    <transition name="modal-slide">
      <modal
        v-if="showModal"
        @close="showModal = false"
        :isVisible="showModal"
        :coverID="Number(selectID)"
        topic="主题"
        sup="补充内容"
        token="your_jwt_token_here"
      />
    </transition>
  </div>
</template>

<script>
import Modal from '@/components/ModalView.vue'
export default {
  components: {
    Modal
  },
  data () {
    return {
      selectedColor: '全部',
      selectedStyle: '全部',
      emps: [],
      showModal: false,
      selectID: 1
    }
  },
  methods: {
    handleChange () {
      // 当颜色选择变化时触发
      if (this.selectedColor === '全部' && this.selectedStyle === '全部') {
        this.fetchEmps('', '')
      } else if (this.selectedColor === '全部') {
        this.fetchEmps('', this.selectedStyle)
      } else if (this.selectedStyle === '全部') {
        this.fetchEmps(this.selectedColor, '')
      } else {
        this.fetchEmps(this.selectedColor, this.selectedStyle)
      }
    },
    fetchEmps (cl, stl) {
      this.$axios
        .post('/ppt-template', {
          colour: cl,
          tag: stl
        })
        .then((response) => {
          console.log(response.data)
          this.emps = response.data.data
        })
        .catch((error) => {
          console.error('请求失败：', error)
        })
    }
  },
  mounted () {
    this.$axios
      .post('/ppt-template', {
        colour: '',
        tag: ''
      })
      .then((response) => {
        console.log(response.data)
        this.emps = response.data.data
      })
      .catch((error) => {
        console.error('请求失败：', error)
      })
  }
}
</script>

<style>
#title {
  text-align: center;
  padding: 80px;
  padding-bottom: 40px;
  background-image: radial-gradient(#c4d8ec, #a2c6e4, #b1d0e6);
  /* background-image: linear-gradient(to bottom right, #2775b6, #1677b3); */
}
#title h1,
h2 {
  color: white;
}
.radioGroup {
  padding-left: 2px;
  margin: 40px;
  margin-bottom: 0px;
  margin-left: 120px;
}
.el-radio-button {
  size: 30px;
}
.grid-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-around; /* 或者使用 space-between, center 等 */
  align-items: flex-start;
  padding: 100px; /* 增加九宫格整体的外边距 */
  padding-top: 40px;
  width: calc(100% - 200px); /* 减去padding，保持总宽度为100% */
}
.grid-item {
  flex: 0 0 calc(33.33% - 40px); /* 每个项目的宽度和基础宽度减去间隔 */
  margin: 10px; /* 间隔 */
  position: relative; /* 为图片下方的说明文字设置定位参考 */
  transition: transform 0.3s ease-in-out;
}
.grid-item:hover {
  transform: scale(1.05); /* 放大到原始大小的1.2倍 */
}
.grid-item img {
  width: 100%; /* 图片宽度设置为容器宽度 */
  height: auto; /* 图片高度自动调整以保持比例 */
  display: block; /* 消除图片下方的默认间距 */
  max-width: 100%; /* 限制图片最大宽度为容器宽度 */
}
.image-caption {
  position: absolute; /* 绝对定位以相对于图片定位 */
  bottom: 0; /* 贴紧图片底部 */
  left: 0; /* 从左边开始 */
  right: 0; /* 从右边开始，确保文本水平居中 */
  padding: 10px; /* 文本内边距 */
  background-color: rgba(84, 131, 201, 0.5); /* 半透明背景色 */
  color: white; /* 文本颜色 */
  text-align: center; /* 文本水平居中 */
  margin: 0; /* 移除外边距 */
  font-size: 14px; /* 字体大小 */
}
.modal-slide-enter-active, .modal-slide-leave-active {
  transition: transform 0.3s ease; /* 添加一个过渡效果，使动画更平滑 */
}

.modal-slide-enter, .modal-slide-leave-to {
  transform: translateY(100%); /* 设置初始位置和结束位置 */
}
</style>
