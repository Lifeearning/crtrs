import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/login/index.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/register/index.vue')
    },
    {
      path: '/home',
      name: 'home',
      component: () => import('../views/home/index.vue'),
      children: [
        {
          path: '/index',
          name: 'index',
          component: () => import('../views/home/index/index.vue')
        },
        {
          path: '/publish',
          name: 'publish',
          component: () => import('../views/home/publish/index.vue')
        },
        {
          path: '/bookstall',
          name: 'bookstall',
          component: () => import('../views/home/bookstall/index.vue')
        },
        {
          path: '/orders',
          name: 'orders',
          component: () => import('../views/home/orders/index.vue')
        },
        {
          path: '/profile',
          name: 'profile',
          component: () => import('../views/home/profile/index.vue'),
        },
        {
          path: '/edit',
          name: 'profileEdit',
          component: () => import('../views/home/edit/index.vue')
        }
      ]
    },
    {
      path: '/detail/:bId',
      name: 'detail',
      component: () => import('../views/details/shopDetails/index.vue')
    },
    {
      path: '/sold/:bId',
      name: 'sold',
      component: () => import('../views/details/soldDetails/index.vue')
    }
  ],
})

router.beforeEach((to, form, next) => {
  if (to.path === '/login' || to.path === '/register') {
    next()  // 若是跳转到login，则直接跳转
  } else {
    // 获取token
    const token = localStorage.getItem('token')
    if (token) {
      next()  // 若有token，则跳转到其他页面
    } else {
      alert('请先登录！')
      next('/login')  // 若没有token，则跳转到login
    }
  }
})

export default router
