<template>
  <div id="app">
    <Header v-if="$route.name !== 'Edit'"></Header>
    <transition :name="transitionName">
      <router-view></router-view>
    </transition>
    <!-- 这个标签负责渲染匹配的路由组件 -->
  </div>
</template>

<script>
import Header from '@/components/HeaderView.vue'

export default {
  name: 'App',
  components: {
    Header
  },
  data () {
    return {
      transitionName: ''
    }
  },
  watch: {
    // 使用watch 监听$router的变化
    $route (to, from) {
      // 如果to索引大于from索引,判断为前进状态,反之则为后退状态
      console.log(to, 'to')
      console.log(from, 'from')
      if (to.meta.index > from.meta.index) {
        // 设置动画名称
        this.transitionName = 'slide-left'
      } else {
        this.transitionName = 'slide-right'
      }
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

#app {
  width: 100%;
  height: 100%;
}

.el-menu-demo {
  position: fixed;
  top: 0;
  width: 100%;
  /* 确保 Header 组件占据整个浏览器宽度 */
  z-index: 1000;
  /* 可能需要调整，确保 Header 在其他内容上方显示 */
  /* 添加其他样式，比如边框、阴影等 */
}
.slide-right-enter-active,
.slide-right-leave-active,
.slide-left-enter-active,
.slide-left-leave-active {
  will-change: transform;
  transition: all 500ms;
  position: fixed;
}

.slide-right-enter {
  opacity: 0;
  transform: translate3d(-100%, 0, 0);
}

.slide-right-leave-active {
  opacity: 0;
  transform: translate3d(100%, 0, 0);
}

.slide-left-enter {
  opacity: 0;
  transform: translate3d(100%, 0, 0);
}

.slide-left-leave-active {
  opacity: 0;
  transform: translate3d(-100%, 0, 0);
}

</style>
