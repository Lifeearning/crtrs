<template>
  <div class="book-detail-container">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-icon size="60" color="#409EFF"><Loading /></el-icon>
      <p>加载商品详情中...</p>
    </div>

    <!-- 加载失败 -->
    <div v-else-if="error" class="error-container">
      <el-icon size="50" color="#f56c6c"><WarningFilled /></el-icon>
      <p>商品详情加载失败，请刷新重试</p>
      <el-button type="primary" @click="fetchBookDetail">刷新</el-button>
    </div>

    <!-- 详情内容（加载成功） -->
    <div v-else class="detail-content">
      <!-- 顶部返回栏 -->
      <div class="header-bar">
        <el-button type="text" @click="router.back()" class="back-btn">
          <el-icon><ArrowLeft /></el-icon> 返回
        </el-button>
        <h1 class="detail-title">《{{ bookDetail.title }}》</h1>
        <!-- 已售出标签 -->
        <el-tag type="danger" size="large" class="sold-tag">已售出</el-tag>
      </div>

      <!-- 主体内容 -->
      <div class="main-content">
        <!-- 左侧图片区域 -->
        <div class="image-area">
          <div v-if="!bookDetail.bimageUrl || bookDetail.bimageUrl === '' || bookDetail.bimageUrl === null" class="image-placeholder">
            <el-icon size="80"><Notebook /></el-icon>
            <p>暂无书籍图片</p>
          </div>
          <el-image
            v-else
            :src="bookDetail.bimageUrl"
            fit="contain"
            class="book-detail-img"
            lazy
          >
            <template #error>
              <div class="image-placeholder">
                <el-icon size="80"><PictureFilled /></el-icon>
                <p>图片加载失败</p>
              </div>
            </template>
          </el-image>
        </div>

        <!-- 右侧信息区域 -->
        <div class="info-area">
          <!-- 卖家信息 -->
          <div class="seller-info">
            <div class="avatar-wrapper">
              <div v-if="!bookDetail.fromCountAvatar" class="avatar-placeholder">
                <el-icon size="40"><UserFilled /></el-icon>
              </div>
              <el-image
                v-else
                :src="bookDetail.fromCountAvatar"
                fit="cover"
                class="seller-avatar"
                lazy
              >
                <template #error>
                  <div class="avatar-placeholder">
                    <el-icon size="40"><UserFilled /></el-icon>
                  </div>
                </template>
              </el-image>
            </div>
            <div class="seller-name">卖家：{{ bookDetail.ucount || '未知卖家' }}</div>
          </div>

          <!-- 书籍核心信息 -->
          <div class="book-core-info">
            <div class="price-box">
              <span class="price-label">售价：</span>
              <span class="price-value">¥{{ bookDetail.price?.toFixed(2) || '0.00' }}</span>
              <!-- 已售出提示 -->
              <div class="sold-tip-box">
                <el-icon color="#f56c6c"><SoldOut /></el-icon>
                <span>该商品已售出</span>
              </div>
            </div>

            <div class="info-grid">
              <div class="info-item">
                <span class="label">ISBN码：</span>
                <span class="value">{{ bookDetail.isbn || '暂无' }}</span>
              </div>
              <div class="info-item">
                <span class="label">书籍作者：</span>
                <span class="value">{{ bookDetail.author || '暂无' }}</span>
              </div>
              <div class="info-item">
                <span class="label">出版社：</span>
                <span class="value">{{ bookDetail.publisher || '暂无' }}</span>
              </div>
              <div class="info-item">
                <span class="label">所属专业：</span>
                <span class="value">{{ bookDetail.major || '暂无' }}</span>
              </div>
              <div class="info-item">
                <span class="label">关联课程：</span>
                <span class="value">{{ bookDetail.course || '暂无' }}</span>
              </div>
            </div>
          </div>

          <!-- 备注信息 -->
          <div class="note-area">
            <h3 class="note-title">书籍备注</h3>
            <div class="note-content">
              {{ bookDetail.note || '卖家未添加备注信息' }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowLeft, Notebook, PictureFilled, UserFilled, WarningFilled, Loading, SoldOut } from '@element-plus/icons-vue'
import service from '@/api'

// 路由相关
const router = useRouter()
const route = useRoute()
const bId = route.params.bId 

// 状态管理
const loading = ref(true)
const error = ref(false)
const bookDetail = ref({}) 

// 获取商品详情
const fetchBookDetail = async () => {
  loading.value = true
  error.value = false
  try {
    const response = await service.get('/index/getDetails', {
      params: { bId } 
    })
    bookDetail.value = response
  } catch (err) {
    console.error('商品详情加载失败：', err)
    error.value = true
  } finally {
    loading.value = false
  }
}

// 页面初始化加载数据
onMounted(() => {
  fetchBookDetail()
})
</script>

<style scoped>
.book-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: calc(100vh - 40px);
}

/* 加载状态样式 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
  color: #666;
}
.loading-container p {
  margin-top: 20px;
	font-size: 16px;
}

/* 错误状态样式 */
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
  color: #666;
}
.error-container p {
  margin: 20px 0;
	font-size: 16px;
}

/* 头部返回栏 */
.header-bar {
  display: flex;
  align-items: center;
	margin-bottom: 30px;
	gap: 20px;
  position: relative;
}
.back-btn {
  color: #666;
	font-size: 16px;
	display: flex;
	align-items: center;
	gap: 8px;
}
.back-btn:hover {
  color: #1989fa;
}
.detail-title {
  font-size: 24px;
	color: #333;
	margin: 0;
	font-weight: 600;
}
/* 已售出标签 */
.sold-tag {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
}

/* 主体内容布局 */
.main-content {
  display: flex;
	gap: 40px;
	background: #fff;
	border-radius: 12px;
	padding: 30px;
	box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

/* 左侧图片区域 */
.image-area {
  flex: 0 0 300px;
	height: 400px;
	background-color: #f9f9f9;
	border-radius: 8px;
	overflow: hidden;
	display: flex;
	align-items: center;
	justify-content: center;
}
.book-detail-img {
  width: 100%;
	height: 100%;
}
.image-placeholder {
  width: 100%;
	height: 100%;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	color: #999;
}
.image-placeholder p {
  margin-top: 20px;
	font-size: 18px;
}

/* 右侧信息区域 */
.info-area {
  flex: 1;
	display: flex;
	flex-direction: column;
	gap: 30px;
}

/* 卖家信息 */
.seller-info {
  display: flex;
	align-items: center;
	gap: 15px;
}
.avatar-wrapper {
  width: 60px;
	height: 60px;
	border-radius: 50%;
	overflow: hidden;
	background-color: #f9f9f9;
	display: flex;
	align-items: center;
	justify-content: center;
}
.seller-avatar {
  width: 100%;
	height: 100%;
}
.avatar-placeholder {
  width: 100%;
	height: 100%;
	display: flex;
	align-items: center;
	justify-content: center;
	color: #999;
}
.seller-name {
  font-size: 16px;
	color: #333;
	font-weight: 500;
}

/* 书籍核心信息 */
.book-core-info {
  display: flex;
	flex-direction: column;
	gap: 15px;
}
.price-box {
  padding-bottom: 20px;
	border-bottom: 1px solid #f0f0f0;
  position: relative;
}
.price-label {
  font-size: 18px;
	color: #666;
	margin-right: 10px;
}
.price-value {
  font-size: 32px;
	color: #f56c6c;
	font-weight: bold;
}
/* 已售出提示框 */
.sold-tip-box {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 15px;
  color: #f56c6c;
  font-size: 16px;
}

.info-grid {
  display: grid;
	grid-template-columns: repeat(2, 1fr);
	gap: 20px;
}
.info-item {
  display: flex;
	flex-direction: column;
	gap: 5px;
}
.info-item .label {
  font-size: 14px;
	color: #999;
}
.info-item .value {
  font-size: 16px;
	color: #333;
}

/* 备注区域 */
.note-area {
  padding-top: 20px;
	border-top: 1px solid #f0f0f0;
}
.note-title {
  font-size: 18px;
	color: #333;
	margin: 0 0 15px;
	font-weight: 500;
}
.note-content {
  font-size: 16px;
	color: #666;
	line-height: 1.8;
	white-space: pre-line;
	background-color: #f9f9f9;
	padding: 15px;
	border-radius: 8px;
	min-height: 100px;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .main-content {
    flex-direction: column;
    padding: 20px;
    gap: 20px;
  }
  .image-area {
    flex: none;
    width: 100%;
    height: 300px;
  }
  .info-grid {
    grid-template-columns: 1fr;
  }
  .detail-title {
    font-size: 20px;
  }
  .price-value {
    font-size: 28px;
  }
  .sold-tag {
    position: static;
    transform: none;
    margin-left: auto;
  }
  .header-bar {
    flex-wrap: wrap;
  }
}
</style>