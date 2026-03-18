import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevtools from 'vite-plugin-vue-devtools'
import Components from 'unplugin-vue-components/vite'
import AutoImport from 'unplugin-auto-import/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

export default defineConfig({
  plugins: [
    vue(),
    // vueDevtools(),
    // 自动导入Element Plus的“服务类组件”（ElMessageBox、ElMessage等）
    AutoImport({
      resolvers: [
        ElementPlusResolver()
      ]
    }),
    // 自动导入UI组件
    Components({
      resolvers: [
        ElementPlusResolver()
      ]
    })
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    host: '0.0.0.0',
    open: true,
    port: 8888,
  }
})