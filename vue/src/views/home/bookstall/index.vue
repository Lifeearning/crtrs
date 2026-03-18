<template>
  <div class="bookstall-page">
    <div class="bookstall-container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h2>我的书摊</h2>
        <el-divider direction="left"></el-divider>
      </div>

      <!-- 筛选区域 -->
      <div class="filter-container">
        <el-form :inline="true" :model="filterForm" class="filter-form">
          <el-form-item label="售出状态：">
            <el-select
              v-model="filterForm.isSold"
              placeholder="全部售出状态"
              clearable
              class="filter-input"
            >
              <el-option label="未售出" value="0"></el-option>
              <el-option label="已售出" value="1"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleFilter">筛选</el-button>
            <el-button @click="resetFilter">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 商品列表 -->
      <div class="goods-list">
        <el-card
          v-for="(book, index) in filteredBookList"
          :key="index"
          class="book-card"
          hover
          @click="goToDetail(book.bid, book.isSold)"
        >
          <!-- 商品图片区域 -->
          <div class="book-img-wrapper">
            <div
              v-if="!book.bimageUrl || book.bimageUrl === '' || book.bimageUrl === null"
              class="image-placeholder"
            >
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

          <!-- 商品信息区域 -->
          <div class="book-info">
            <h4 class="book-title" :title="book.title">《{{ book.title }}》</h4>
            <p class="book-isbn">ISBN：{{ book.isbn || '暂无' }}</p>
            <p class="book-author">作者：{{ book.author || '暂无' }}</p>
            <p class="book-publisher">出版社：{{ book.publisher || '暂无' }}</p>
            <div class="book-price">¥{{ book.price }}</div>

            <!-- 状态标签区域 -->
            <div class="book-status">
              <el-tag
                :type="book.isSold === 1 ? 'danger' : 'primary'"
                size="small"
              >
                {{ book.isSold === 1 ? '已售出' : '未售出' }}
              </el-tag>
            </div>

            <div class="book-actions">
              <button
                class="native-btn delete-btn"
                @click.stop="handleDelete(book.bid)"
                :disabled="book.isSold === 1"
              >
                <span class="btn-text">删除</span>
              </button>
            </div>
          </div>
        </el-card>

        <!-- 空数据提示 -->
        <div v-if="filteredBookList.length === 0" class="empty-tip">
          <el-icon size="50"><Notebook /></el-icon>
          <p>暂无已发布的书籍信息</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Notebook, PictureFilled } from '@element-plus/icons-vue'
import service from '@/api'
import { useRouter } from 'vue-router'

const router = useRouter()
const uid = localStorage.getItem('uid')

// 筛选表单 - 移除audit字段
const filterForm = ref({
  isSold: ''
})

// 商品列表
const bookList = ref([])
const filteredBookList = ref([])

// 获取商品列表
const fetchMyBookList = async () => {
  if (!uid) {
    alert('请先登录后再操作！')
    router.push('/login')
    return
  }

  try {
    const response = await service.get('/index/getMyBooks', {
      params: { uid }
    })
    console.log('我的书摊商品列表：', response);
    
    if (response.code === 0) {
      bookList.value = response.data || []
      filteredBookList.value = [...bookList.value]
    } else {
      alert('获取书籍列表失败：' + response.msg)
    }
  } catch (error) {
    alert('获取我的书摊数据失败，请稍后重试！')
    console.error('获取书摊数据异常：', error)
  }
}

// 筛选逻辑 - 移除audit相关筛选
const handleFilter = () => {
  let result = [...bookList.value]

  if (filterForm.value.isSold !== '') {
    result = result.filter(book => book.isSold === Number(filterForm.value.isSold))
  }

  filteredBookList.value = result
  if (result.length === 0) {
    alert('暂无符合筛选条件的书籍')
  }
}

// 重置筛选 - 移除audit字段重置
const resetFilter = () => {
  filterForm.value = {
    isSold: ''
  }
  filteredBookList.value = [...bookList.value]
  alert('筛选条件已重置，展示所有书籍')
}

// 跳转到书籍详情页
const goToDetail = (bid, isSold) => {
  if (isSold === 1) {
    // 已售出商品跳转到新的详情页
    router.push({
      name: 'sold', // 注意需要在路由中配置该名称
      params: { bId: bid }
    })
  } else {
    // 未售出商品跳转到原有详情页
    router.push({
      name: 'detail',
      params: { bId: bid }
    })
  }
}

// 删除商品
const handleDelete = async (bid) => {
  try {
    const confirmResult = confirm('确定要删除该商品吗？删除后不可恢复！')
    if (confirmResult) {
      const response = await service.post('/index/deleteBook', { bid })
      console.log(response);
      
      if (response.code === 0) {
        bookList.value = bookList.value.filter(book => book.bid !== bid)
        filteredBookList.value = filteredBookList.value.filter(book => book.bid !== bid)
      } else {
        alert('删除失败：' + response.msg)
      }
    }
  } catch (error) {
    if (error && error.toString() !== 'Cancel') {
      alert('删除操作失败，请稍后重试！')
      console.error('❌ 删除商品失败：', error)
    }
  }
}

// 页面挂载加载数据
onMounted(() => {
  fetchMyBookList()
})
</script>

<style scoped>
/* 基础布局样式 */
.bookstall-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}
.bookstall-container {
  padding: 20px 0;
}
.page-header h2 {
  font-size: 22px;
  color: #333;
  margin: 0 0 10px;
}

/* 筛选区域样式 */
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
  width: 180px;
}

/* 商品列表样式 */
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

/* 商品卡片样式 */
.book-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: default;
  border: 1px solid #f0f0f0;
  min-height: 420px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}
.book-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}
.book-img-wrapper {
  height: 200px;
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
  font-size: 14px;
}
.book-info {
  padding: 18px;
  flex-grow: 1;
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
  font-weight: 500;
}
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

/* 状态标签样式 */
.book-status {
  display: flex;
  gap: 8px;
  margin: 10px 0;
}

/* 按钮容器 */
.book-actions {
  display: flex;
  gap: 10px;
  margin-top: auto;
  padding-top: 15px;
}

/* 核心：原生按钮样式（美观且适配） */
.native-btn {
  flex: 1;
  border: none;
  border-radius: 8px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px; /* 图标和文字间距 */
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.25s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

/* 编辑按钮样式（保留但未使用，如需删除可移除） */
.edit-btn {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  color: #fff;
}
.edit-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #3399ff, #55aaff);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(64, 158, 255, 0.2);
}
.edit-btn:disabled {
  background: #e5e9f2;
  color: #c0c6d0;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 删除按钮样式 */
.delete-btn {
  background: linear-gradient(135deg, #f56c6c, #f78989);
  color: #fff;
}
.delete-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #f45c5c, #f67979);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(245, 108, 108, 0.2);
}
.delete-btn:disabled {
  background: #fde2e2;
  color: #f5c6cb;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 按钮文字 */
.btn-text {
  font-size: 14px;
}

/* 响应式适配 */
@media (max-width: 992px) {
  .filter-input {
    width: 160px;
  }
  .book-card {
    min-height: 400px;
  }
}
@media (max-width: 768px) {
  .goods-list {
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  }
  .book-card {
    min-height: 380px;
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
  .book-actions {
    flex-direction: column;
  }
  .native-btn {
    width: 100%;
    height: 40px;
  }
}
</style>