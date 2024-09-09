import Vue from 'vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import App from './App.vue'
import router from './router'
import './plugins/element.js'
import axios from 'axios'

Vue.config.productionTip = false

Vue.use(ElementUI)

Vue.prototype.$axios = axios

if (process.env.NODE_ENV === 'development') {
  // axios.defaults.baseURL = 'http://localhost:8080/api'
} else {
  axios.defaults.baseURL = '/api'
}

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
