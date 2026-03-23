<template>
  <div class="book-detail-container">
    <div v-if="loading" class="loading-container">
      <el-icon size="60" color="#409EFF"><Loading /></el-icon>
      <p>加载商品详情中...</p>
    </div>

    <div v-else-if="error" class="error-container">
      <el-icon size="50" color="#f56c6c"><WarningFilled /></el-icon>
      <p>商品详情加载失败，请刷新重试</p>
      <el-button type="primary" @click="fetchBookDetail">刷新</el-button>
    </div>

    <div v-else class="detail-content">
      <div class="header-bar">
        <el-button type="text" @click="router.back()" class="back-btn">
          <el-icon><ArrowLeft /></el-icon> 返回
        </el-button>
        <h1 class="detail-title">《{{ bookDetail.title }}》</h1>
      </div>

      <div class="main-content">
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

        <div class="info-area">
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

          <div class="book-core-info">
            <div class="price-box">
              <span class="price-label">售价：</span>
              <span class="price-value">¥{{ bookDetail.price?.toFixed(2) || '0.00' }}</span>
              <span class="balance-value">我的余额：¥{{ balanceNum.toFixed(2) }}</span>
            </div>

            <div v-show="tipText" class="tip-box" :class="tipType === 'success' ? 'success-tip' : 'error-tip'">
              {{ tipText }}
            </div>

            <div class="buy-btn-box">
              <el-button 
                type="primary" 
                size="large" 
                class="buy-btn"
                @click="handleBuy"
                :disabled="!bookDetail.price || bookDetail.price <= 0 || isSelfBook"
              >
                立即购买
              </el-button>
            </div>

            <div v-show="confirmDialogVisible" class="confirm-dialog-mask">
              <div class="confirm-dialog">
                <div class="dialog-header">购买确认</div>
                <div class="dialog-content">
                  确认购买《{{ bookDetail.title }}》吗？价格：¥{{ (bookDetail.price || 0).toFixed(2) }}，当前余额充足
                </div>
                <div class="dialog-footer">
                  <el-button @click="cancelBuy" class="cancel-btn">取消</el-button>
                  <el-button type="primary" @click="confirmBuy" class="confirm-btn">确认购买</el-button>
                </div>
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

          <div class="note-area">
            <h3 class="note-title">书籍备注</h3>
            <div class="note-content">
              {{ bookDetail.note || '卖家未添加备注信息' }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-show="successDialogVisible" class="success-dialog-mask">
      <div class="success-dialog">
        <div class="success-icon">✓</div>
        <div class="success-title">购买成功！</div>
        <div class="success-desc">
          已扣除¥{{ (bookDetail.price || 0).toFixed(2) }}，剩余余额¥{{ balanceNum.toFixed(2) }}
          <br/>
          <span class="jump-tip">将在 {{ countDown }} 秒后跳转到订单页面...</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowLeft, Notebook, PictureFilled, UserFilled, WarningFilled, Loading } from '@element-plus/icons-vue'
import service from '@/api'

const router = useRouter()
const route = useRoute()
const bId = route.params.bId 

const balanceNum = ref(parseFloat(localStorage.getItem('balance')) || 0)
const currentUserUcount = ref(localStorage.getItem('ucount') || '')

const loading = ref(true)
const error = ref(false)
const bookDetail = ref({})

// 判断是否是自己的商品
const isSelfBook = computed(() => {
  if (!currentUserUcount.value || !bookDetail.value?.ucount) return false
  return currentUserUcount.value === bookDetail.value.ucount
})

const tipText = ref('')
const tipType = ref('error')
const confirmDialogVisible = ref(false)
const successDialogVisible = ref(false)
const countDown = ref(3)
let timer = null

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

// 购买点击
const handleBuy = () => {
  tipText.value = ''
  
  // 拦截：自己不能买自己的商品
  if (isSelfBook.value) {
    tipText.value = '无法购买自己发布的商品！'
    tipType.value = 'error'
    setTimeout(() => tipText.value = '', 3500)
    return
  }

  const bookPrice = bookDetail.value.price || 0
  if (bookPrice <= 0) {
    tipText.value = '商品价格异常，无法购买！'
    tipType.value = 'error'
    setTimeout(() => tipText.value = '', 3000)
    return
  }

  if (balanceNum.value < bookPrice) {
    tipText.value = `余额不足！当前余额¥${balanceNum.value.toFixed(2)}，需¥${bookPrice.toFixed(2)}`
    tipType.value = 'error'
    setTimeout(() => tipText.value = '', 3500)
    return
  }

  confirmDialogVisible.value = true
}

// 显示成功弹窗
const showSuccessDialog = () => {
  countDown.value = 3
  successDialogVisible.value = true
  
  if (timer) clearInterval(timer)
  
  timer = setInterval(() => {
    countDown.value--
    if (countDown.value <= 0) {
      clearInterval(timer)
      timer = null
      successDialogVisible.value = false
      router.push('/orders')
    }
  }, 1000)
}

// 确认购买
const confirmBuy = async () => {
  try{
    const bookPrice = bookDetail.value.price || 0
    balanceNum.value -= bookPrice
    localStorage.setItem('balance', balanceNum.value.toString())
    
    confirmDialogVisible.value = false

    const orderInfo = {
      bid: Number(bId),
      price: Number(bookPrice)
    }
    await service.post('/order/createOrder', orderInfo)
    
    showSuccessDialog()
    
  }catch(err){
    console.error('购买失败：', err)
    tipText.value = '购买失败，请稍后重试！'
    tipType.value = 'error'
    setTimeout(() => tipText.value = '', 3000)
    confirmDialogVisible.value = false
  }
}

// 取消购买
const cancelBuy = () => {
  confirmDialogVisible.value = false
}

onMounted(() => {
  fetchBookDetail()
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.book-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: calc(100vh - 40px);
}

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

.header-bar {
  display: flex;
  align-items: center;
	margin-bottom: 30px;
	gap: 20px;
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

.main-content {
  display: flex;
	gap: 40px;
	background: #fff;
	border-radius: 12px;
	padding: 30px;
	box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

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

.info-area {
  flex: 1;
	display: flex;
	flex-direction: column;
	gap: 30px;
}

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

.book-core-info {
  display: flex;
	flex-direction: column;
	gap: 15px;
}
.price-box {
  padding-bottom: 20px;
	border-bottom: 1px solid #f0f0f0;
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
.balance-value {
  margin-left: 20px;
	font-size: 16px;
	color: #67c23a;
	font-weight: 500;
}

.tip-box {
  width: 100%;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 14px;
  text-align: center;
  margin: 5px 0;
  line-height: 1.5;
}
.error-tip {
  background: #fef0f0;
  color: #f56c6c;
  border: 1px solid #fde2e2;
}
.success-tip {
  background: #f0f9eb;
  color: #67c23a;
  border: 1px solid #e1f3d8;
}

.buy-btn-box {
  margin-top: 5px;
}
.buy-btn {
  width: 180px;
	height: 48px;
	font-size: 18px;
	border-radius: 8px;
}

.confirm-dialog-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}
.confirm-dialog {
  width: 90%;
  max-width: 400px;
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}
.dialog-header {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 15px;
  text-align: center;
}
.dialog-content {
  font-size: 16px;
  color: #666;
  line-height: 1.5;
  margin-bottom: 20px;
  text-align: center;
}
.dialog-footer {
  display: flex;
  gap: 10px;
  justify-content: center;
}
.cancel-btn, .confirm-btn {
  width: 120px;
  height: 40px;
  border-radius: 8px;
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

.success-dialog-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 99999;
  backdrop-filter: blur(2px);
}

.success-dialog {
  width: 90%;
  max-width: 420px;
  background: #ffffff;
  border-radius: 16px;
  padding: 40px 30px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  text-align: center;
  animation: dialogFadeIn 0.3s ease-out;
}

@keyframes dialogFadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.success-icon {
  font-size: 60px;
  color: #67c23a;
  margin-bottom: 20px;
  font-weight: bold;
}

.success-title {
  font-size: 24px;
  color: #333333;
  font-weight: 600;
  margin-bottom: 15px;
}

.success-desc {
  font-size: 16px;
  color: #666666;
  line-height: 1.6;
}

.jump-tip {
  display: block;
  margin-top: 10px;
  color: #999999;
  font-size: 14px;
}

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
  .balance-value {
    display: block;
    margin-left: 0;
    margin-top: 10px;
  }
  .buy-btn {
    width: 100%;
  }
  .confirm-dialog {
    width: 85%;
    padding: 15px;
  }
  .cancel-btn, .confirm-btn {
    width: 100px;
    height: 36px;
    font-size: 14px;
  }
  .success-dialog {
    padding: 30px 20px;
  }
  .success-title {
    font-size: 20px;
  }
  .success-desc {
    font-size: 14px;
  }
}
</style>