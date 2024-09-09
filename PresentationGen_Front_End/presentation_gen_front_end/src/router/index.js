import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '@/views/HomeView.vue'
import Auth from '@/views/AuthView.vue'
import User from '@/views/UserView.vue'
import Edit from '@/views/EditView.vue'
import PPT from '@/views/PPTView.vue'
import Person from '@/views/PersonView.vue'
import History from '@/views/HistoryView.vue'
import Help from '@/views/HelpView.vue'

Vue.use(VueRouter)

const router = new VueRouter({
  // mode: 'history',
  routes: [{
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: Home,
    meta: {
      index: 1
    }
  },
  {
    path: '/history',
    name: 'History',
    component: History,
    meta: {
      index: 2
    }
  },
  {
    path: '/ppt',
    name: 'PPT',
    component: PPT,
    meta: {
      index: 3
    }
  },
  {
    path: '/help',
    name: 'Help',
    component: Help,
    meta: {
      index: 4
    }
  },
  {
    path: '/user',
    name: 'User',
    component: User,
    meta: {
      index: 5
    }

  },
  {
    path: '/auth',
    name: 'Auth',
    component: Auth,
    meta: {
      index: 5
    }
  },
  {
    path: '/edit/:markdownData/:templates/:id',
    name: 'Edit',
    component: Edit
  },

  {
    path: '/person',
    name: 'Person',
    component: Person,
    meta: {
      index: 5
    }
  }

  ]
})

export default router
