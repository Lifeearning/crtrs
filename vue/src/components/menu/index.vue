<template>
  <!-- 顶部导航栏 -->
  <div class="app-header">
    <!-- 左侧：应用图标和名称 -->
    <div class="header-left">
      <el-icon class="app-icon">
        <ShoppingBag />
      </el-icon>
      <span class="app-name">校园书转</span>
    </div>

    <!-- 中间：主导航菜单 -->
    <div class="header-center">
      <el-menu
        :default-active="activePath"
        mode="horizontal"
        class="main-menu"
        @select="handleMenuSelect"
        background-color="#f8f9fa"
        text-color="#333"
        active-text-color="#165DFF"
      >
        <el-menu-item index="/index">
          <el-icon><House /></el-icon>
          <span>首页</span>
          <span class="shortcut">Ctrl+H</span>
        </el-menu-item>
        <el-menu-item index="/publish">
          <el-icon><Upload /></el-icon>
          <span>发布商品</span>
          <span class="shortcut">Ctrl+P</span>
        </el-menu-item>
        <el-menu-item index="/bookstall">
          <el-icon><Sell /></el-icon>
          <span>书摊管理</span>
          <span class="shortcut">Ctrl+M</span>
        </el-menu-item>
        <el-menu-item index="/orders">
          <el-icon><ShoppingCart /></el-icon>
          <span>我的订单</span>
          <span class="shortcut">Ctrl+O</span>
        </el-menu-item>
        <el-menu-item index="/profile">
          <el-icon><User /></el-icon>
          <span>个人信息</span>
          <span class="shortcut">Ctrl+I</span>
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 右侧：功能区 -->
    <div class="header-right">
      <!-- 退出登录 -->
      <el-button type="danger" round @click="handleLogout">退出登录</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  ShoppingBag, Upload, ShoppingCart,
  User, House, Sell // 移除了Search图标导入
} from '@element-plus/icons-vue'

// 状态管理
const activePath = ref('')

// 路由相关
const route = useRoute()
const router = useRouter()

// 初始化当前激活菜单
onMounted(() => {
  activePath.value = route.path
  // 监听路由变化更新激活状态
  watch(route, (newRoute) => {
    activePath.value = newRoute.path
  })
})

// 菜单选择处理
const handleMenuSelect = (index) => {
  router.push(index)
}

// 注册快捷键
onMounted(() => {
  document.addEventListener('keydown', (e) => {
    if (e.ctrlKey) {
      switch (e.key.toLowerCase()) {
        case 'h':
          e.preventDefault()
          router.push('/index')
          break
        case 'p':
          e.preventDefault()
          router.push('/publish')
          break
        case 'm':
          e.preventDefault()
          router.push('/bookstall')
          break
        case 'o':
          e.preventDefault()
          router.push('/orders')
          break
        case 'i':
          e.preventDefault()
          router.push('/profile')
          break
      }
    }
  })
})

// 退出登录处理
const handleLogout = () => {
  // 清除用户信息
  localStorage.removeItem('token')
  localStorage.removeItem('uid')
  localStorage.removeItem('ucount')
  localStorage.removeItem('upho')
  localStorage.removeItem('balance')
  localStorage.removeItem('avatar')
  // 跳转到登录页
  router.push('/login')
}
</script>

<style scoped>
.app-header {
  display: flex;
  align-items: center;
  height: 56px;
  padding: 0 24px;
  background-color: #f8f9fa;
  border-bottom: 1px solid #eaecef;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  user-select: none; /* 禁止选中文字，符合桌面应用体验 */
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-right: 32px;
}

.app-icon {
  font-size: 20px;
  color: #165DFF;
}

.app-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.header-center {
  flex: 1;
}

.main-menu {
  border-bottom: none !important;
}

.main-menu .el-menu-item {
  padding: 0 20px;
  height: 56px;
  line-height: 56px;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 快捷键样式 */
.shortcut {
  font-size: 12px;
  color: #86909c;
  margin-left: 8px;
  opacity: 0.8;
}

.main-menu .el-menu-item:hover {
  background-color: #eef2ff;
}

.main-menu .el-menu-item.is-active {
  background-color: #e8f3ff;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-avatar {
  cursor: pointer;
}

.avatar {
  width: 36px;
  height: 36px;
  border: 1px solid #e5e6eb;
}

.user-popover-menu {
  width: 160px;
  border-radius: 4px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

/* 滚动条样式优化（Windows风格） */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>