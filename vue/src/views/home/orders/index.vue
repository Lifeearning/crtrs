<template>
  <div class="my-orders-page">
    <div class="orders-container">
      <div class="page-header">
        <h2>我的订单</h2>
        <el-divider direction="left"></el-divider>
      </div>

      <el-tabs v-model="activeTab" class="orders-tabs" type="card">
        <!-- 我购买的订单 -->
        <el-tab-pane label="我购买的订单" name="buy">
          <div class="order-list buy-order-list">
            <div v-if="buyOrderList.length === 0" class="empty-tip">
              <el-icon size="50"><ShoppingCart /></el-icon>
              <p>暂无购买订单</p>
            </div>

            <el-card
              v-for="(order, index) in buyOrderList"
              :key="index"
              class="order-card"
              hover
            >
              <div class="order-header">
                <div class="order-basic">
                  <span class="order-id">订单编号：{{ order.orderId }}</span>
                  <span class="order-time">下单时间：{{ order.orderTime }}</span>
                  <span class="order-status">交易完成</span>
                </div>
              </div>

              <div class="order-book-info">
                <div class="book-img-wrapper">
                  <div v-if="!order.book.bimageUrl" class="image-placeholder">
                    <el-icon size="40"><Notebook /></el-icon>
                    <p>暂无书籍图片</p>
                  </div>
                  <el-image
                    v-else
                    :src="order.book.bimageUrl"
                    fit="contain"
                    class="book-img"
                  >
                    <template #error>
                      <div class="image-placeholder">
                        <el-icon size="40"><PictureFilled /></el-icon>
                        <p>图片加载失败</p>
                      </div>
                    </template>
                  </el-image>
                </div>

                <div class="book-base-info">
                  <h4 class="book-title" :title="order.book.title">《{{ order.book.title }}》</h4>
                  <p class="book-isbn">ISBN：{{ order.book.isbn }}</p>
                  <p class="book-price">成交价格：¥{{ order.price }}</p>
                </div>

                <div class="seller-info">
                  <h5>卖方信息</h5>
                  <p>卖家昵称：{{ order.sellerInfo.ucountPub }}</p>
                  <p>联系方式：{{ order.sellerInfo.uphoPub || '暂无' }}</p>
                </div>
              </div>

              <div class="evaluate-area">
                <h5 class="evaluate-title">订单评价</h5>
                <div v-if="!order.evaluation && !order.isEditing" class="un-evaluate">
                  <p>您尚未评价该订单，点击下方按钮进行评价</p>
                  <el-button type="primary" size="small" @click="handleEditEvaluate(order)">去评价</el-button>
                </div>

                <div v-if="order.isEditing" class="edit-evaluate">
                  <el-form :model="currentEvaluate" class="evaluate-form">
                    <el-form-item label="评价星级：" label-width="100px">
                      <el-rate
                        v-model="currentEvaluate.score"
                        :max="5"
                        show-text
                        text-color="#ff9900"
                        active-color="#ff9900"
                        void-color="#e6e6e6"
                        void-icon="Star"
                        icon="Star"
                        class="star-rate"
                        style="display: inline-flex; gap: 4px;"
                      >
                        <template #text="{ value }">
                          <span class="star-text">{{ value }}星</span>
                        </template>
                      </el-rate>
                    </el-form-item>

                    <el-form-item label="评价内容：" label-width="100px">
                      <el-input
                        v-model="currentEvaluate.content"
                        type="textarea"
                        rows="3"
                        placeholder="请输入评价内容（最多200字）"
                        maxlength="200"
                        show-word-limit
                        style="width: 100%;"
                      />
                    </el-form-item>

                    <el-form-item class="evaluate-btn-group" label-width="80px">
                      <el-button type="primary" @click="handleSubmitEvaluate(order)">提交评价</el-button>
                      <el-button @click="cancelEdit(order)">取消</el-button>
                    </el-form-item>
                  </el-form>
                </div>

                <div v-if="order.evaluation && !order.isEditing" class="evaluated">
                  <div class="evaluate-result">
                    <div class="score-display">
                      <span>评价星级：</span>
                      <el-rate
                        v-model="order.evaluation.opinionStar"
                        :max="5"
                        disabled
                        active-color="#ff9900"
                        void-color="#e6e6e6"
                        void-icon="Star"
                        icon="Star"
                        style="display: inline-flex; gap: 4px; vertical-align: middle;"
                      >
                        <template #text="{ value }">
                          <span class="star-text">{{ value }}星</span>
                        </template>
                      </el-rate>
                    </div>
                    <div class="content-display">
                      <p>评价内容：{{ order.evaluation.opinion || '无评价内容' }}</p>
                      <p class="evaluate-time">评价时间：{{ order.evaluation.opinionTime }}</p>
                    </div>
                  </div>
                  <el-button size="small" @click="handleEditEvaluate(order)">修改评价</el-button>
                </div>
              </div>
            </el-card>
          </div>
        </el-tab-pane>

        <!-- 我卖出的订单 -->
        <el-tab-pane label="我卖出的订单" name="sell">
          <div class="order-list sell-order-list">
            <div v-if="sellOrderList.length === 0" class="empty-tip">
              <el-icon size="50"><Shop /></el-icon>
              <p>暂无卖出订单</p>
            </div>

            <el-card
              v-for="(order, index) in sellOrderList"
              :key="index"
              class="order-card"
              hover
            >
              <div class="order-header">
                <div class="order-basic">
                  <span class="order-id">订单编号：{{ order.orderId }}</span>
                  <span class="order-time">下单时间：{{ order.orderTime }}</span>
                  <span class="order-status">交易完成</span>
                </div>
              </div>

              <div class="order-book-info">
                <div class="book-img-wrapper">
                  <div v-if="!order.book.bimageUrl" class="image-placeholder">
                    <el-icon size="40"><Notebook /></el-icon>
                    <p>暂无书籍图片</p>
                  </div>
                  <el-image
                    v-else
                    :src="order.book.bimageUrl"
                    fit="contain"
                    class="book-img"
                  >
                    <template #error>
                      <div class="image-placeholder">
                        <el-icon size="40"><PictureFilled /></el-icon>
                        <p>图片加载失败</p>
                      </div>
                    </template>
                  </el-image>
                </div>

                <div class="book-base-info">
                  <h4 class="book-title" :title="order.book.title">《{{ order.book.title }}》</h4>
                  <p class="book-isbn">ISBN：{{ order.book.isbn }}</p>
                  <p class="book-price">成交价格：¥{{ order.price }}</p>
                </div>

                <div class="buyer-info">
                  <h5>买方信息</h5>
                  <p>买家昵称：{{ order.buyerInfo.ucountBuy }}</p>
                  <p>联系方式：{{ order.buyerInfo.uphoBuy || '暂无' }}</p>
                </div>
              </div>

              <div class="buyer-evaluate-area">
                <h5 class="evaluate-title">买方评价</h5>
                <div v-if="!order.evaluation" class="no-buyer-evaluate">
                  <el-icon size="20"><Clock /></el-icon>
                  <p>买家尚未对该订单进行评价</p>
                </div>
                <div v-if="order.evaluation" class="buyer-evaluate-result">
                  <div class="score-display">
                    <span>评价星级：</span>
                    <el-rate
                      v-model="order.evaluation.opinionStar"
                      :max="5"
                      disabled
                      active-color="#ff9900"
                      void-color="#e6e6e6"
                      void-icon="Star"
                      icon="Star"
                      style="display: inline-flex; gap: 4px; vertical-align: middle;"
                    >
                      <template #text="{ value }">
                        <span class="star-text">{{ value }}星</span>
                      </template>
                    </el-rate>
                  </div>
                  <div class="content-display">
                    <p>评价内容：{{ order.evaluation.opinion || '无评价内容' }}</p>
                    <p class="evaluate-time">评价时间：{{ order.evaluation.opinionTime }}</p>
                  </div>
                </div>
              </div>
            </el-card>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { 
  ShoppingCart, Shop, Notebook, PictureFilled, 
  Star, Clock 
} from '@element-plus/icons-vue'
import service from '@/api'

// 激活的标签页
const activeTab = ref('buy')
// 当前编辑的评价数据
const currentEvaluate = reactive({
  score: 5,
  content: ''
})

// 初始化为空数组（删除模拟数据）
const buyOrderList = ref([])
const sellOrderList = ref([])
// 获取本地存储的ucount
const currentUcount = ref(localStorage.getItem('ucount') || '')

// 格式化UTC时间为本地时间
const formatOrderTime = (utcTime) => {
  if (!utcTime) return ''
  const date = new Date(utcTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  }).replace(/\//g, '-')
}

// 处理后端返回的订单数据，按ucount分类
const processOrderData = (orderList) => {
  const buyOrders = []
  const sellOrders = []

  if (!Array.isArray(orderList) || !currentUcount.value) return

  orderList.forEach(item => {
    // orderId补零
    const formatOrderId=(id)=>{
      if(!id) return ''
      return id.toString().padStart(8,'0')
    }
    // 构造统一的订单结构
    const order = {
      orderId: formatOrderId(item.orderId), // 订单编号
      orderTime: formatOrderTime(item.orderTime), // 格式化时间
      price: item.price || 0,
      book: {
        title: item.title || '',
        isbn: item.isbn || '',
        bimageUrl: item.bimageUrl || ''
      },
      // 评价逻辑：opinion为null则未评价
      evaluation: item.opinion !== null ? {
        opinionStar: item.opinionStar || 0,
        opinion: item.opinion || '',
        opinionTime: formatOrderTime(item.opinionTime)
      } : null,
      isEditing: false
    }

    // 区分买入/卖出订单：对比本地ucount和后端的ucountBuy/ucountPub
    if (item.ucountBuy === currentUcount.value) {
      // 我购买的订单 → 显示卖家信息（ucountPub/uphoPub）
      order.sellerInfo = {
        ucountPub: item.ucountPub || '',
        uphoPub: item.uphoPub || ''
      }
      buyOrders.push(order)
    } else if (item.ucountPub === currentUcount.value) {
      // 我卖出的订单 → 显示买家信息（ucountBuy/uphoBuy）
      order.buyerInfo = {
        ucountBuy: item.ucountBuy || '',
        uphoBuy: item.uphoBuy || ''
      }
      sellOrders.push(order)
    }
  })

  // 赋值给渲染数组（覆盖空数组）
  buyOrderList.value = buyOrders
  sellOrderList.value = sellOrders
}

// 获取后端订单数据
const fetchMyBookList = async () => {
  if (!currentUcount.value) {
    alert('请先登录！')
    return
  }

  try {
    const response = await service.get('/order/orderInfo')
    console.log('后端返回的订单数据：', response)
    
    // 后端直接返回订单数组，传入处理函数
    processOrderData(response)
  } catch (error) {
    alert('获取订单数据失败，请稍后重试！')
    console.error('获取订单异常：', error)
  }
}

// 评价相关方法（保持不变）
const handleEditEvaluate = (order) => {
  order.isEditing = true
  if (order.evaluation) {
    currentEvaluate.score = order.evaluation.opinionStar
    currentEvaluate.content = order.evaluation.opinion
  } else {
    currentEvaluate.score = 5
    currentEvaluate.content = ''
  }
}

const cancelEdit = (order) => {
  order.isEditing = false
  currentEvaluate.score = 5
  currentEvaluate.content = ''
}

const handleSubmitEvaluate = (order) => {
  if (!currentEvaluate.score) {
    alert('请选择评价星级')
    return
  }

  const evaluateData = {
    opinionStar: currentEvaluate.score,
    opinion: currentEvaluate.content.trim(),
    opinionTime: new Date().toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    }).replace(/\//g, '-')
  }

  order.evaluation = evaluateData
  order.isEditing = false
  alert(order.evaluation ? '评价修改成功' : '评价提交成功')
  currentEvaluate.score = 5
  currentEvaluate.content = ''

  const submitData = {
    orderId: order.orderId,
    opinion: evaluateData.opinion,
    opinionStar: evaluateData.opinionStar,
    opinionTime: evaluateData.opinionTime
  }

  // 提交评价到后端
  service.post('/order/submitOpinion', submitData).then(res => {
    alert('评价提交成功')
  }).catch(err => {
    alert('评价提交失败，请稍后重试')
  })
}

// 页面挂载时请求数据
onMounted(() => {
  fetchMyBookList()
})
</script>

<style scoped>
.my-orders-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: 100%;
}

.orders-container {
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.page-header h2 {
  font-size: 22px;
  color: #333;
  margin: 0 0 10px;
}

.orders-tabs {
  margin-top: 20px;
}

.order-list {
  margin-top: 20px;
}

.empty-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  color: #999;
}

.empty-tip p {
  margin-top: 15px;
  font-size: 16px;
}

.order-card {
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.order-card:hover {
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.order-header {
  padding: 10px 15px;
  border-bottom: 1px solid #f0f0f0;
}

.order-basic {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: center;
}

.order-id, .order-time {
  font-size: 14px;
  color: #666;
}

.order-status {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  background-color: #f0f9ff;
  color: #1989fa;
}

.order-book-info {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  padding: 15px;
  align-items: center;
}

.book-img-wrapper {
  width: 100px;
  height: 120px;
  background-color: #f9f9f9;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.book-img {
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
  margin-top: 10px;
  font-size: 12px;
}

.book-base-info {
  flex: 1;
  min-width: 200px;
}

.book-title {
  font-size: 16px;
  color: #333;
  margin: 0 0 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.book-isbn {
  font-size: 12px;
  color: #999;
  margin: 0 0 8px;
}

.book-price {
  font-size: 16px;
  color: #f56c6c;
  font-weight: bold;
}

.seller-info, .buyer-info {
  min-width: 180px;
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.seller-info h5, .buyer-info h5 {
  font-size: 14px;
  color: #333;
  margin: 0 0 8px;
  padding-bottom: 5px;
  border-bottom: 1px solid #e6e6e6;
}

.seller-info p, .buyer-info p {
  font-size: 13px;
  color: #666;
  margin: 0 0 4px;
}

.evaluate-area, .buyer-evaluate-area {
  padding: 15px;
  border-top: 1px solid #f0f0f0;
}

.evaluate-title {
  font-size: 14px;
  color: #333;
  margin: 0 0 10px;
  padding-bottom: 5px;
  border-bottom: 1px solid #e6e6e6;
}

.un-evaluate {
  padding: 10px 0;
  color: #666;
}

.edit-evaluate {
  margin-top: 10px;
}

.evaluate-form {
  max-width: 600px;
}

.star-rate {
  font-size: 18px;
}

.star-text {
  font-size: 14px;
  margin-left: 8px;
  color: #ff9900;
  font-weight: 500;
}

.evaluate-btn-group {
  margin-top: 10px;
}

.evaluated, .buyer-evaluate-result {
  padding: 10px 0;
}

.score-display {
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
}

.content-display {
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 4px;
  margin-bottom: 10px;
}

.content-display p {
  font-size: 14px;
  color: #666;
  margin: 0 0 5px;
}

.evaluate-time {
  font-size: 12px;
  color: #999 !important;
}

.no-buyer-evaluate {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #999;
  padding: 10px 0;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .orders-container {
    padding: 20px 15px;
  }

  .order-book-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .book-img-wrapper {
    width: 80px;
    height: 100px;
  }

  .seller-info, .buyer-info {
    width: 100%;
    min-width: unset;
  }

  .evaluate-form {
    max-width: 100%;
  }
}
</style>