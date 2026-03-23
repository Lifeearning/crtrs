<template>
  <div class="second-hand-book-home">
    <!-- 头部轮播图 -->
    <div class="banner-container">
      <el-carousel height="400px" indicator-position="bottom" autoplay interval="5000">
        <el-carousel-item v-for="(item, index) in displayBannerList" :key="index">
          <div class="banner-item" @click="handleBannerClick(item)">
            <!-- 轮播图图片空值/加载失败处理 -->
            <div v-if="!item.bImageUrl || item.bImageUrl === '' || item.bImageUrl === null" class="banner-image-placeholder">
              <el-icon size="60"><PictureFilled /></el-icon>
              <p>暂无轮播图</p>
            </div>
            <el-image
              v-else
              :src="item.bImageUrl"
              fit="cover"
              class="banner-img"
            >
              <template #error>
                <div class="banner-image-placeholder">
                  <el-icon size="60"><PictureFilled /></el-icon>
                  <p>图片加载失败</p>
                </div>
              </template>
            </el-image>
            <div class="banner-mask">
              <h3>《{{ item.title }}》</h3>
              <p class="price">¥{{ item.price }}</p>
              <p class="desc">{{ item.desc }}</p>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>
    <!-- 商品列表区域 -->
    <div class="goods-container">
      <div class="goods-header">
        <h2>精确找书</h2>
        <el-divider direction="left"></el-divider>
        <div class="filter-container">
          <el-form :inline="true" :model="filterForm" class="filter-form">
            <el-form-item label="书籍名称：">
              <el-input
                v-model="filterForm.title"
                placeholder="输入书籍名称关键词"
                clearable
                class="filter-input"
              ></el-input>
            </el-form-item>
            <el-form-item label="ISBN码：">
              <el-input
                v-model="filterForm.isbn"
                placeholder="输入ISBN码关键词"
                clearable
                class="filter-input"
              ></el-input>
            </el-form-item>
            <el-form-item label="出版社：">
              <el-input
                v-model="filterForm.publisher"
                placeholder="输入出版社关键词"
                clearable
                class="filter-input"
              ></el-input>
            </el-form-item>
            <el-form-item label="书籍作者：">
              <el-input
                v-model="filterForm.author"
                placeholder="输入作者关键词"
                clearable
                class="filter-input"
              ></el-input>
            </el-form-item>
            <el-form-item label="所属专业：">
              <el-select
                v-model="filterForm.major"
                placeholder="输入/选择专业"
                filterable
                allow-create
                clearable
                class="filter-input"
              >
                <el-option
                  v-for="item in majorReferenceList"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="关联课程：">
              <el-select
                v-model="filterForm.course"
                placeholder="输入/选择课程"
                filterable
                allow-create
                clearable
                class="filter-input"
              >
                <el-option
                  v-for="item in courseReferenceList"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleFilter">筛选</el-button>
              <el-button @click="resetFilter">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
      <div class="goods-list">
        <el-card
          v-for="(book, index) in filteredBookList"
          :key="index"
          class="book-card"
          @click="goToDetail(book.bid)"
          hover
        >
          <div class="book-img-wrapper">
            <div v-if="!book.bimageUrl || book.bimageUrl === '' || book.bimageUrl === null" class="image-placeholder">
              <el-icon size="40"><Notebook /></el-icon>
              <p>暂无书籍图片</p>
            </div>
            <el-image
              v-else
              :src="book.bimageUrl"
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
          <div class="book-info">
            <h4 class="book-title" :title="book.title">《{{ book.title }}》</h4>
            <p class="book-isbn">ISBN：{{ book.isbn || '暂无' }}</p>
            <p class="book-author">作者：{{ book.author || '暂无' }}</p>
            <p class="book-publisher">出版社：{{ book.publisher || '暂无' }}</p>
            <div class="book-price">¥{{ book.price }}</div>
            <div class="book-major-course">
              <span v-if="book.major" class="tag">专业：{{ book.major }}</span>
              <span v-if="book.course" class="tag">课程：{{ book.course }}</span>
            </div>
          </div>
        </el-card>
        <div v-if="filteredBookList.length === 0" class="empty-tip">
          <el-icon size="50"><Notebook /></el-icon>
          <p>暂无符合筛选条件的书籍</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, computed } from 'vue'
import { Notebook, PictureFilled } from '@element-plus/icons-vue'
import service from '@/api'
import { useRouter } from 'vue-router'
const router = useRouter()

// 系统预制轮播数据（标记isPreset区分）
const presetBannerList = ref([
  {
    bid: 1,
    bImageUrl: '/images/轮播图测试1.png',
    title: '算法导论（原书第3版）',
    price: 89.9,
    desc: '畅销编程书籍 | 付费推广',
    isPreset: true
  },
  {
    bid: 2,
    bImageUrl: '/images/轮播图测试2.png',
    title: 'Vue.js设计与实现',
    price: 75.5,
    desc: '前端热门书籍 | 付费推广',
    isPreset: true
  },
  {
    bid: 3,
    bImageUrl: '/images/轮播图测试3.png',
    price: 68.8,
    desc: '新手入门必看 | 畅销推荐',
    isPreset: true
  }
])

const majorReferenceList = ref([
  { label: '计算机科学与技术', value: '计算机科学与技术' },
  { label: '软件工程', value: '软件工程' },
  { label: '电子信息工程', value: '电子信息工程' },
  { label: '自动化', value: '自动化' },
  { label: '数据科学与大数据技术', value: '数据科学与大数据技术' }
])
const courseReferenceList = ref([
  { label: '数据结构与算法', value: '数据结构与算法' },
  { label: '操作系统', value: '操作系统' },
  { label: '计算机网络', value: '计算机网络' },
  { label: '编程语言（Python/Java/JS）', value: '编程语言（Python/Java/JS）' },
  { label: '机器学习', value: '机器学习' }
])
const filterForm = ref({
  title: '',
  isbn: '',
  publisher: '',
  author: '',
  major: '',
  course: ''
})
const bookList = ref([])

// 计算属性：处理轮播图展示数据（取bookList前3条，不足则用预制数据补充）
const displayBannerList = computed(() => {
  const realBannerData = bookList.value.slice(0, 3).map(item => ({
    ...item,
    isPreset: false,
    bImageUrl: item.bimageUrl || item.bImageUrl,
    desc: item.desc || '优质二手书籍推荐'
  }))
  
  if (realBannerData.length < 3) {
    const needAdd = 3 - realBannerData.length
    const addPreset = presetBannerList.value.slice(0, needAdd)
    realBannerData.push(...addPreset)
  }
  
  return realBannerData
})

// 页面初始化时，获取后端书籍列表数据
const fetchBookList = async () => {
  try {
    const response = await service.get("/index/getAll")
    if (Array.isArray(response)) {
      bookList.value = response
    } else {
      console.error('❌ 后端返回数据不是数组：', response)
      bookList.value = []
    }
    filteredBookList.value = [...bookList.value]
  } catch (error) {
    bookList.value = []
    filteredBookList.value = []
  }
}
onMounted(() => {
  fetchBookList()
})

const filteredBookList = ref([...bookList.value])
const handleFilter = () => {
  let result = [...bookList.value]
  
  if (filterForm.value.title) {
    const titleKey = filterForm.value.title.toLowerCase()
    result = result.filter(book => book.title.toLowerCase().includes(titleKey))
  }
  
  if (filterForm.value.isbn) {
    result = result.filter(book => book.isbn.includes(filterForm.value.isbn))
  }
  
  if (filterForm.value.publisher) {
    const publisherKey = filterForm.value.publisher.toLowerCase()
    result = result.filter(book => book.publisher.toLowerCase().includes(publisherKey))
  }
  
  if (filterForm.value.author) {
    const authorKey = filterForm.value.author.toLowerCase()
    result = result.filter(book => book.author.toLowerCase().includes(authorKey))
  }
  
  if (filterForm.value.major) {
    result = result.filter(book => book.major.includes(filterForm.value.major))
  }
  
  if (filterForm.value.course) {
    result = result.filter(book => book.course.includes(filterForm.value.course))
  }
  
  filteredBookList.value = result
}
const resetFilter = () => {
  filterForm.value = {
    title: '',
    isbn: '',
    publisher: '',
    author: '',
    major: '',
    course: ''
  }
  filteredBookList.value = [...bookList.value]
}

// 提示框
const showCustomAlert = () => {
  // 遮罩层
  const overlay = document.createElement('div')
  overlay.style.cssText = `
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    z-index: 9998;
    animation: fadeIn 0.2s ease;
  `
  
  // 提示框主体
  const alertBox = document.createElement('div')
  alertBox.style.cssText = `
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 320px;
    padding: 30px 20px;
    background: #fff;
    border-radius: 16px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
    z-index: 9999;
    text-align: center;
    animation: zoomIn 0.3s ease;
  `
  
  alertBox.innerHTML = `
    <div style="margin-bottom: 16px;">
      <svg width="50" height="50" viewBox="0 0 24 24" fill="none" stroke="#ff7d00" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <circle cx="12" cy="12" r="10"></circle>
        <line x1="12" y1="8" x2="12" y2="12"></line>
        <line x1="12" y1="16" x2="12.01" y2="16"></line>
      </svg>
    </div>
    <h3 style="margin: 0 0 8px; font-size: 18px; color: #333; font-weight: 600;">暂无商品</h3>
    <p style="margin: 0 0 20px; font-size: 14px; color: #666;">敬请期待</p>
    <button id="closeAlert" style="padding: 10px 30px; background: #ff7d00; color: #fff; border: none; border-radius: 8px; font-size: 14px; cursor: pointer;">确定</button>
  `
  
  // 动画样式
  const style = document.createElement('style')
  style.textContent = `
    @keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
    @keyframes zoomIn { from { opacity: 0; transform: translate(-50%, -50%) scale(0.9); } to { opacity: 1; transform: translate(-50%, -50%) scale(1); } }
  `
  document.head.appendChild(style)
  
  // 添加到页面
  document.body.appendChild(overlay)
  document.body.appendChild(alertBox)
  
  // 关闭功能
  const close = () => {
    document.body.removeChild(overlay)
    document.body.removeChild(alertBox)
    document.head.removeChild(style)
  }
  
  document.getElementById('closeAlert').onclick = close
  overlay.onclick = close
  
  // 3秒自动关闭
  setTimeout(close, 3000)
}

// 轮播图点击事件
const handleBannerClick = (item) => {
  if (item.isPreset) {
    showCustomAlert()
    return
  }
  goToDetail(item.bid)
}

// 跳转到书籍详情页
const goToDetail = (bid) => {
  router.push({name: 'detail', params: { bId: bid }})
}
</script>

<style scoped>
.second-hand-book-home {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}
.banner-container {
  margin: 20px 0;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
.banner-item {
  position: relative;
  height: 100%;
  cursor: pointer;
}
.banner-img {
  width: 100%;
  height: 100%;
}
/* 轮播图图片占位样式 */
.banner-image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
  background-color: #f9f9f9;
}
.banner-image-placeholder p {
  margin-top: 10px;
  font-size: 16px;
}
.banner-mask {
  position: absolute;
  bottom: 0;
	left: 0;
	right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  color: #fff;
  padding: 20px;
}
.banner-mask h3 {
  font-size: 24px;
  margin: 0 0 10px;
}
.banner-mask .price {
  font-size: 20px;
  color: #f56c6c;
  margin: 0 0 5px;
}
.banner-mask .desc {
  font-size: 14px;
  opacity: 0.9;
}
.goods-header {
  margin: 30px 0 20px;
}
.goods-header h2 {
  font-size: 22px;
  color: #333;
  margin: 0;
  display: inline-block;
}
.filter-container {
  margin: 20px 0;
  padding: 15px;
  background: #f9f9f9;
	border-radius: 8px;
}
.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  align-items: center;
}
.filter-input {
  width: 200px;
}
.goods-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
  min-height: 400px;
}
.empty-tip {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #999;
}
.empty-tip p {
  margin-top: 15px;
  font-size: 16px;
}

.book-card {
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid #e6e6e6;
  min-height: 380px; /* 固定最小高度，确保所有卡片整齐 */
  display: flex;
  flex-direction: column;
}
.book-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}
.book-img-wrapper {
  height: 200px;
  background-color: #f9f9f9;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0; /* 图片区域不压缩 */
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
  font-size: 14px;
}
.image-error {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #f56c6c;
  background-color: #fef0f0;
}
.book-info {
  padding: 15px;
  flex-grow: 1; /* 内容区域自动填充剩余空间 */
  display: flex;
  flex-direction: column;
}
.book-title {
  font-size: 16px;
  color: #333;
  margin: 0 0 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
/* 信息行样式优化：统一间距和溢出处理 */
.book-isbn, .book-author, .book-publisher {
  font-size: 12px;
  color: #999;
  margin: 0 0 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.book-price {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
  margin: 10px 0;
}
/* 专业/课程标签样式优化 */
.book-major-course {
  font-size: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: auto; /* 标签区域靠底显示 */
  padding-top: 10px;
}
.book-major-course .tag {
  background-color: #f0f2f5;
  color: #666;
  padding: 2px 8px;
  border-radius: 12px; /* 圆角标签更美观 */
  white-space: nowrap;
}
/* 响应式调整 */
@media (max-width: 992px) {
  .filter-input {
    width: 160px;
  }
  .book-card {
    min-height: 350px;
  }
}
@media (max-width: 768px) {
  .banner-container {
    height: 250px;
  }
  .el-carousel {
    height: 250px !important;
  }
  .goods-list {
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  }
  .book-card {
    min-height: 300px;
  }
  .book-img-wrapper {
    height: 160px;
  }
  .filter-input {
    width: 100%;
  }
  .filter-form {
    flex-direction: column;
    align-items: flex-start;
  }
  .book-title {
    font-size: 14px;
  }
  .book-price {
    font-size: 16px;
  }
}
</style>