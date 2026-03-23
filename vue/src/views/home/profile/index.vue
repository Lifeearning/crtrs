<template>
  <div class="personal-info-page">
    <!-- 页面容器 -->
    <div class="info-container">
      <!-- 1. 用户基础信息区域 -->
      <div class="user-info-card">
        <el-card class="base-info-card">
          <!-- 头像与核心信息 -->
          <div class="user-base-info">
            <div class="avatar-wrapper">
              <!-- 头像展示（支持默认头像/加载失败处理） -->
              <el-avatar
                :size="100"
                :src="userInfo.avatarUrl"
                class="user-avatar"
              >
                <el-icon size="40"><User /></el-icon>
              </el-avatar>
            </div>
            <div class="user-detail-info">
              <div class="info-item">
                <span class="label">账号：</span>
                <span class="value">{{ userInfo.nickname }}</span>
              </div>
              <div class="info-item">
                <span class="label">联系方式：</span>
                <span class="value">{{ userInfo.contact }}</span>
              </div>
              <div class="info-item">
                <span class="label">账户余额：</span>
                <span class="value balance">¥{{ userInfo.balance.toFixed(2) }}</span>
              </div>
              <el-button
                type="primary"
                size="small"
                @click="goToEditInfo"
                class="edit-btn"
              >
                修改个人信息
              </el-button>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 2. 订单预览区域 -->
      <div class="order-preview-card">
        <el-card>
          <div class="card-header">
            <h3>
              订单消息预览
              <el-tag size="small" type="info" effect="light" class="tips-tag">
                <el-icon size="12"><Clock /></el-icon>
                仅显示最新5条数据
              </el-tag>
            </h3>
            <el-button
              type="text"
              @click="goToOrders"
              class="more-btn"
            >
              查看全部 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
          <!-- 订单预览标签页 -->
          <el-tabs v-model="activeOrderTab" type="card" class="order-preview-tabs">
            <!-- 购买订单预览 -->
            <el-tab-pane label="我购买的订单" name="buy">
              <div v-if="buyOrderPreview.length === 0" class="empty-preview">
                <el-icon size="30"><ShoppingCart /></el-icon>
                <p>暂无购买订单</p>
              </div>
              <div class="order-preview-list" v-else>
                <div
                  v-for="(order, index) in buyOrderPreview"
                  :key="index"
                  class="preview-item"
                >
                  <span class="order-id">订单：{{ order.orderId }}</span>
                  <span class="book-title">《{{ order.book.title }}》</span>
                  <span class="order-price">¥{{ order.price }}</span>
                  <span class="order-status" :class="order.statusClass">{{ order.status }}</span>
                  <span class="order-time">下单时间：{{ order.orderTime }}</span>
                </div>
              </div>
            </el-tab-pane>
            <!-- 卖出订单预览 -->
            <el-tab-pane label="我卖出的订单" name="sell">
              <div v-if="sellOrderPreview.length === 0" class="empty-preview">
                <el-icon size="30"><Shop /></el-icon>
                <p>暂无卖出订单</p>
              </div>
              <div class="order-preview-list" v-else>
                <div
                  v-for="(order, index) in sellOrderPreview"
                  :key="index"
                  class="preview-item"
                >
                  <span class="order-id">订单：{{ order.orderId }}</span>
                  <span class="book-title">《{{ order.book.title }}》</span>
                  <span class="order-price">¥{{ order.price }}</span>
                  <span class="order-status" :class="order.statusClass">{{ order.status }}</span>
                  <span class="order-time">下单时间：{{ order.orderTime }}</span>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </div>

      <!-- 3. 收益统计图表区域 -->
      <div class="profit-chart-card">
        <el-card>
          <div class="card-header">
            <h3>
              收益统计
              <el-tag size="small" type="primary" effect="light" class="tips-tag">
                <el-icon size="12"><Calendar /></el-icon>
                近5月
              </el-tag>
            </h3>
            <!-- 图表类型切换按钮 -->
            <div class="chart-type-switch">
              <el-button
                size="small"
                :type="chartType === 'bar' ? 'primary' : 'default'"
                @click="switchChartType('bar')"
              >
                <el-icon><Histogram /></el-icon> 柱状图
              </el-button>
              <el-button
                size="small"
                :type="chartType === 'line' ? 'primary' : 'default'"
                @click="switchChartType('line')"
              >
                <el-icon><TrendCharts /></el-icon> 折线图
              </el-button>
            </div>
          </div>
          <!-- ECharts容器 -->
          <div ref="profitChartRef" class="profit-chart" id="profitChart"></div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { 
  User, ShoppingCart, Shop, ArrowRight, 
  Histogram, TrendCharts, Clock, Calendar
} from '@element-plus/icons-vue'
import service from '@/api'

const router = useRouter()

// 用户基础信息数据
const userInfo = reactive({
  avatarUrl: localStorage.getItem('avatar'),
  nickname: localStorage.getItem('ucount'),
  contact: localStorage.getItem('upho'),
  balance: Number(localStorage.getItem('balance'))
})

// 页面加载时自动从后端获取最新余额，覆盖本地缓存（确保数据一致性）
const getUserInfo = async () => {
  try {
    const response = await service.get('/user/getInfo')
    
    userInfo.balance = parseFloat(response.balance) || 0
    localStorage.setItem('balance', userInfo.balance.toString())
    userInfo.avatarUrl = response.avatar || userInfo.avatarUrl
    localStorage.setItem('avatar', userInfo.avatarUrl)
  } catch (err) {
    console.error('获取余额失败：', err)
    userInfo.balance = 0
    localStorage.setItem('balance', '0')
  }
}

// 订单预览数据
const activeOrderTab = ref('buy')
const buyOrderPreview = ref([])
const sellOrderPreview = ref([])
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
    minute: '2-digit'
  }).replace(/\//g, '-')
}

// 格式化订单编号（补零）
const formatOrderId = (id) => {
  if (!id) return ''
  return id.toString().padStart(8, '0')
}

// 处理订单数据（筛选最新5条）
const processOrderPreviewData = (orderList) => {
  const buyOrders = []
  const sellOrders = []
  if (!Array.isArray(orderList) || !currentUcount.value) return
  
  const sortedOrders = [...orderList].sort((a, b) => {
    return new Date(b.orderTime) - new Date(a.orderTime)
  })
  
  sortedOrders.forEach(item => {
    const order = {
      orderId: formatOrderId(item.orderId),
      orderTime: formatOrderTime(item.orderTime),
      price: item.price || 0,
      book: {
        title: item.title || '未知书籍',
        isbn: item.isbn || ''
      },
      status: '交易完成',
      statusClass: 'status-success'
    }
    if (item.ucountBuy === currentUcount.value) {
      buyOrders.push(order)
    } else if (item.ucountPub === currentUcount.value) {
      sellOrders.push(order)
    }
  })
  
  buyOrderPreview.value = buyOrders.slice(0, 5)
  sellOrderPreview.value = sellOrders.slice(0, 5)
}

// 获取订单预览数据
const fetchOrderPreview = async () => {
  if (!currentUcount.value) return
  
  try {
    const response = await service.get('/order/orderInfo')
    processOrderPreviewData(response)
  } catch (error) {
    console.error('获取订单预览数据失败：', error)
  }
}

// ECharts 收益图表相关
const profitChartRef = ref(null)
let profitChart = null
const chartType = ref('bar')

// 收益数据
const profitChartData = reactive({
  xAxisData: [],
  yAxisData: []
})

// 从接口获取收益数据
const fetchProfitData = async () => {
  try {
    const res = await service.get('/order/profit')
    profitChartData.xAxisData = res.data.xaxisData
    profitChartData.yAxisData = res.data.yaxisData
  } catch (error) {
    profitChartData.xAxisData = ['2024-01', '2024-02', '2024-03', '2024-04', '2024-05']
    profitChartData.yAxisData = [0, 0, 0, 0, 0]
    console.error('获取收益统计数据失败：', error)
  }
}

// 初始化/更新收益图表
const initProfitChart = () => {
  const chartDom = profitChartRef.value
  if (!chartDom) return
  
  if (!profitChart) {
    profitChart = echarts.init(chartDom)
  }
  
  const option = {
    title: {
      text: '月度收益统计',
      left: 'center',
      textStyle: { fontSize: 14, color: '#666' }
    },
    tooltip: {
      trigger: 'item', 
      formatter: '{b}：¥{c}',
      backgroundColor: 'rgba(255,255,255,0.9)',
      borderColor: '#e5e5e5',
      textStyle: { color: '#333' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: [
      {
        type: 'category',
        data: profitChartData.xAxisData,
        axisLabel: { color: '#666' },
        axisLine: { lineStyle: { color: '#eee' } }
      }
    ],
    yAxis: [
      {
        type: 'value',
        name: '收益（元）',
        nameTextStyle: { color: '#666' },
        axisLabel: {
          color: '#666',
          formatter: '¥{value}'
        },
        axisLine: { lineStyle: { color: '#eee' } },
        splitLine: { lineStyle: { color: '#f5f5f5' } }
      }
    ],
    series: [
      {
        name: '收益',
        type: chartType.value,
        data: profitChartData.yAxisData,
        itemStyle: chartType.value === 'bar' ? {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#409eff' },
            { offset: 0, color: '#69b1ff' }
          ]),
          borderRadius: [4, 4, 0, 0]
        } : {
          color: '#409eff',
          lineStyle: { width: 2 },
          symbol: 'circle',
          symbolSize: 6
        },
        barWidth: '60%',
        emphasis: {
          itemStyle: {
            color: chartType.value === 'bar' 
              ? new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: '#2d8cf0' },
                  { offset: 1, color: '#409eff' }
                ])
              : '#2d8cf0',
            borderWidth: 0
          },
          symbol: chartType.value === 'line' ? 'circle' : undefined,
          symbolSize: chartType.value === 'line' ? 8 : undefined
        }
      }
    ]
  }
  
  profitChart.setOption(option, true)
  window.addEventListener('resize', () => {
    profitChart?.resize()
  })
}

// 切换图表类型
const switchChartType = (type) => {
  chartType.value = type
  initProfitChart()
}

// 事件处理函数
const goToEditInfo = () => {
  router.push('/edit')
}

const goToOrders = () => {
  router.push('/orders')
}

// 生命周期钩子
onMounted(async () => {
  await fetchProfitData()
  initProfitChart()
  fetchOrderPreview()
  getUserInfo() 
})

onUnmounted(() => {
  if (profitChart) {
    profitChart.dispose()
    profitChart = null
  }
  window.removeEventListener('resize', () => {
    profitChart?.resize()
  })
})
</script>

<style scoped>
/* 页面全局样式 */
.personal-info-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.info-container {
  display: grid;
  grid-template-columns: 1fr;
  gap: 20px;
}

/* 卡片通用样式 */
.el-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.card-header h3 {
  font-size: 18px;
  color: #333;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px; /* 标题和标签之间的间距 */
}

/* 新增：提示标签样式 */
.tips-tag {
  margin-left: 8px;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
}

.more-btn {
  color: #409eff;
  font-size: 14px;
}

/* 图表类型切换按钮样式 */
.chart-type-switch {
  display: flex;
  gap: 8px;
}

/* 1. 用户基础信息区域样式 */
.user-info-card {
  width: 100%;
}

.base-info-card {
  padding: 20px;
}

.user-base-info {
  display: flex;
  align-items: center;
  gap: 30px;
  flex-wrap: wrap;
}

.avatar-wrapper {
  flex-shrink: 0;
}

.user-avatar {
  border: 2px solid #f0f0f0;
}

.user-detail-info {
  flex: 1;
  min-width: 280px;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  font-size: 16px;
}

.info-item .label {
  color: #666;
  width: 80px;
  flex-shrink: 0;
}

.info-item .value {
  color: #333;
  font-weight: 500;
}

.balance {
  color: #f56c6c;
  font-size: 18px;
}

.edit-btn {
  margin-top: 10px;
}

/* 2. 订单预览区域样式 */
.order-preview-card {
  width: 100%;
}

.order-preview-tabs {
  margin-top: 10px;
}

.empty-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #999;
}

.empty-preview p {
  margin-top: 10px;
  font-size: 14px;
}

.order-preview-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.preview-item {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 15px;
  padding: 10px;
  border-radius: 4px;
  background-color: #f9f9f9;
  font-size: 14px;
}

.preview-item .order-id {
  color: #666;
  min-width: 180px;
}

.preview-item .book-title {
  color: #333;
  flex: 1;
  min-width: 200px;
}

.preview-item .order-price {
  color: #f56c6c;
  font-weight: bold;
  min-width: 80px;
  text-align: right;
}

.preview-item .order-status {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  min-width: 80px;
  text-align: center;
}

.preview-item .order-time {
  color: #999;
  font-size: 12px;
  flex: 1;
  min-width: 180px;
}

.status-success {
  background-color: #f0f9ff;
  color: #1989fa;
}

/* 3. 收益图表区域样式 */
.profit-chart-card {
  width: 100%;
}

.profit-chart {
  width: 100%;
  height: 400px;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .user-base-info {
    flex-direction: column;
    gap: 20px;
    align-items: flex-start;
  }

  .preview-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .preview-item .order-price,
  .preview-item .order-status,
  .preview-item .order-time {
    text-align: left;
    min-width: unset;
  }

  .profit-chart {
    height: 300px;
  }

  .chart-type-switch {
    flex-wrap: wrap;
  }

  /* 移动端标签适配 */
  .card-header h3 {
    flex-wrap: wrap;
  }

  .tips-tag {
    margin-left: 0;
    margin-top: 4px;
  }
}
</style>