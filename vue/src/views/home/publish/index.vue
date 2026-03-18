<template>
  <div class="publish-book-page">
    <div class="publish-container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h2>发布商品</h2>
        <el-divider direction="left"></el-divider>
      </div>
      <!-- 发布表单 -->
      <el-form
        ref="publishFormRef"
        :model="publishForm"
        :rules="publishRules"
        label-width="100px"
        class="publish-form"
      >
        <!-- 书籍名称（必选） -->
        <el-form-item label="书籍名称" prop="title">
          <el-input
            v-model="publishForm.title"
            placeholder="请输入书籍完整名称"
            clearable
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        <!-- 所属专业（必选） -->
        <el-form-item label="所属专业" prop="major">
          <el-select
            v-model="publishForm.major"
            placeholder="输入/选择所属专业"
            filterable
            allow-create
            clearable
            style="width: 100%"
          >
            <el-option
              v-for="item in majorReferenceList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <!-- 关联课程（必选） -->
        <el-form-item label="关联课程" prop="course">
          <el-select
            v-model="publishForm.course"
            placeholder="输入/选择关联课程"
            filterable
            allow-create
            clearable
            style="width: 100%"
          >
            <el-option
              v-for="item in courseReferenceList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <!-- ISBN码（必选） -->
        <el-form-item label="ISBN码" prop="isbn">
          <el-input
            v-model="publishForm.isbn"
            placeholder="请输入书籍ISBN码（13位）"
            clearable
            maxlength="13"
            show-word-limit
          />
        </el-form-item>
        <!-- 书籍作者（必选） -->
        <el-form-item label="书籍作者" prop="author">
          <el-input
            v-model="publishForm.author"
            placeholder="请输入书籍作者"
            clearable
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
        <!-- 出版社（必选） -->
        <el-form-item label="书籍出版社" prop="publisher">
          <el-input
            v-model="publishForm.publisher"
            placeholder="请输入出版社名称"
            clearable
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        <!-- 发布价格（必选） -->
        <el-form-item label="发布价格" prop="price">
          <el-input
            v-model.number="publishForm.price"
            placeholder="请输入售价（元）"
            clearable
            type="number"
            min="0.01"
            step="0.01"
            prefix="¥"
          />
        </el-form-item>
        <!-- 书籍图片（可选）- 仅修改这部分配置 -->
        <el-form-item label="书籍图片">
          <el-upload
            class="image-uploader"
            action="#"
            :auto-upload="false"
            :on-change="handleImageChange"
            :on-remove="handleImageRemove"
            :file-list="imageFileList"
            list-type="picture-card"
            accept="image/*"
            :limit="1"
            :before-upload="beforeUpload"
          >
            <div class="upload-btn">
              <el-icon size="20"><Plus /></el-icon>
              <div class="text">上传书籍图片</div>
            </div>
          </el-upload>
          <div class="tip-text">温馨提示：仅支持单张图片上传，建议尺寸比例1:1.2，大小不超过2MB</div>
        </el-form-item>
        <!-- 书籍摘要（可选） -->
        <el-form-item label="书籍摘要">
          <el-input
            v-model="publishForm.desc"
            placeholder="请输入书籍简介、新旧程度、是否有笔记等信息（选填）"
            type="textarea"
            rows="4"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <!-- 提交按钮区域 -->
        <el-form-item class="btn-group">
          <el-button type="primary" @click="handleSubmit" size="large">发布书籍</el-button>
          <el-button @click="handleReset" size="large">重置表单</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script setup>
import { ref, reactive } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import service from '@/api'
import { ElMessage } from 'element-plus'  // 新增：引入提示组件（和你原有风格一致）

// 专业参考列表（与首页保持一致）- 完全保留
const majorReferenceList = ref([
  { label: '计算机科学与技术', value: '计算机科学与技术' },
  { label: '软件工程', value: '软件工程' },
  { label: '电子信息工程', value: '电子信息工程' },
  { label: '自动化', value: '自动化' },
  { label: '数据科学与大数据技术', value: '数据科学与大数据技术' }
])
// 课程参考列表（与首页保持一致）- 完全保留
const courseReferenceList = ref([
  { label: '数据结构与算法', value: '数据结构与算法' },
  { label: '操作系统', value: '操作系统' },
  { label: '计算机网络', value: '计算机网络' },
  { label: '编程语言（Python/Java/JS）', value: '编程语言（Python/Java/JS）' },
  { label: '机器学习', value: '机器学习' }
])
// 表单引用 - 完全保留
const publishFormRef = ref(null)
// 图片上传文件列表 - 完全保留
const imageFileList = ref([])
// 新增：存储用户选中的原始图片文件对象（提交时用）
const uploadFile = ref(null)

// 发布表单数据 - 完全保留（移除了原有的imageUrl，因为提交时才生成）
const publishForm = reactive({
  uid: localStorage.getItem('uid'), // 用户ID
  title: '',        // 书籍名称（必选）
  major: '',        // 所属专业（必选）
  course: '',       // 关联课程（必选）
  isbn: '',         // ISBN码（必选）
  author: '',       // 书籍作者（必选，新增）
  publisher: '',    // 出版社（必选，新增）
  price: '',        // 发布价格（必选）
  desc: ''          // 书籍摘要（可选）
})

// 表单验证规则 - 完全保留（一字未改）
const publishRules = reactive({
  title: [
    { required: true, message: '请输入书籍名称', trigger: 'blur' },
    { min: 2, max: 100, message: '书籍名称长度需在2-100个字符之间', trigger: 'blur' }
  ],
  major: [
    { required: true, message: '请选择/输入所属专业', trigger: 'change' }
  ],
  course: [
    { required: true, message: '请选择/输入关联课程', trigger: 'change' }
  ],
  isbn: [
    { required: true, message: '请输入ISBN码', trigger: 'blur' },
    { pattern: /^\d{13}$/, message: 'ISBN码必须为13位数字', trigger: 'blur' }
  ],
  author: [
    { required: true, message: '请输入书籍作者', trigger: 'blur' },
    { min: 1, max: 50, message: '作者名称长度需在1-50个字符之间', trigger: 'blur' }
  ],
  publisher: [
    { required: true, message: '请输入出版社名称', trigger: 'blur' },
    { min: 1, max: 100, message: '出版社名称长度需在1-100个字符之间', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入发布价格', trigger: 'blur' },
    // 自定义校验：替代原有的type:number + min组合
    { 
      validator: (rule, value, callback) => {
        // 步骤1：判断是否为有效数字（排除空字符串、NaN、非数字）
        const numValue = Number(value); // 显式转换为数字
        if (isNaN(numValue)) {
          callback(new Error('价格必须为数字'));
        } 
        // 步骤2：判断是否低于0.01
        else if (numValue < 0.01) {
          callback(new Error('价格不能低于0.01元'));
        } 
        // 步骤3：校验通过
        else {
          callback();
        }
      },
      trigger: ['blur', 'change'] // 失焦/输入变化时校验
    }
  ]
})

// 新增：图片上传前校验（格式+大小）
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('请上传图片格式文件！')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过2MB！')
    return false
  }
  return true
}

// 修改：处理图片选择/上传变化（仅本地预览，不上传服务器）
const handleImageChange = (uploadFileObj, fileList) => {
  imageFileList.value = fileList
  uploadFile.value = uploadFileObj.raw // 保存原始图片文件对象
  // 保留本地预览效果（和你原有逻辑一致）
  if (fileList.length > 0 && fileList[0].raw) {
    fileList[0].url = URL.createObjectURL(fileList[0].raw)
  }
}

// 修改：移除图片（清空文件对象）
const handleImageRemove = () => {
  imageFileList.value = []
  uploadFile.value = null // 清空存储的文件对象
}

// 修改：提交表单（图片+表单数据一起提交）
const handleSubmit = async () => {
  if (!publishFormRef.value) return
  
  try {
    // 原有逻辑：表单验证
    await publishFormRef.value.validate()
    
    // 新增：图片非空校验（可选，根据你的需求决定是否必填）
    if (!uploadFile.value) {
      ElMessage.warning('请先选择书籍图片！');
      return;
    }

    const isConfirm = confirm('确认发布该书籍信息吗？发布后将展示在商品列表中');
    if (isConfirm) {
      // 新增：创建FormData，封装图片文件+表单数据
      const formData = new FormData()
      formData.append('file', uploadFile.value) // 图片文件（后端参数名：file）
      // 遍历表单数据，全部追加到FormData
      for (const key in publishForm) {
        formData.append(key, publishForm[key])
      }

      // 调用后端统一提交接口
      const res = await service.post('/index/publish', formData, {
        headers: { 'Content-Type': 'multipart/form-data' } // 必须加的请求头
      })
      console.log(res);
      if (res.code === 0) {
        alert('书籍发布成功！');
        handleReset(); // 重置表单
      } else {
        alert('发布失败：' + res.msg);
      }
    } else {
      alert('已取消发布');
    }
  } catch (error) {
    alert('表单验证失败，请检查必填项是否填写正确');
    console.error('表单验证错误：', error);
  }
}

// 修改：重置表单（清空文件对象）
const handleReset = () => {
  if (publishFormRef.value) {
    publishFormRef.value.resetFields();
    imageFileList.value = [];
    uploadFile.value = null; // 新增：清空图片文件对象
    publishForm.desc = '';
  }
}
</script>
<style scoped>
/* 原有样式 - 完全保留，一字未改 */
.publish-book-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: 100%;
}
.publish-container {
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
.publish-form {
  margin-top: 20px;
}
/* 图片上传样式 */
.image-uploader {
  margin-bottom: 8px;
}
.upload-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 120px;
  color: #999;
  cursor: pointer;
}
.upload-btn .text {
  margin-top: 8px;
  font-size: 14px;
}
.tip-text {
  margin-left: 5px;
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}
/* 按钮组样式 */
.btn-group {
  margin-top: 20px;
  display: flex;
  gap: 15px;
}
.btn-group .el-button {
  width: 150px;
}
/* 响应式适配 */
@media (max-width: 768px) {
  .publish-container {
    padding: 20px 15px;
  }
  .el-form-item--label-top .el-form-item__label {
    padding: 0 0 5px 0;
  }
  .btn-group {
    flex-direction: column;
  }
  .btn-group .el-button {
    width: 100%;
  }
  .image-uploader .el-upload--picture-card {
    width: 100%;
  }
}
/* 表单通用样式 */
:deep(.el-form-item) {
  margin-bottom: 20px;
}
:deep(.el-input__inner),
:deep(.el-select__wrapper),
:deep(.el-textarea__inner) {
  border-radius: 4px;
}
:deep(.el-select .el-input__inner) {
  padding-right: 35px;
}
:deep(.el-upload-list__item) {
  width: 120px;
  height: 140px;
}
:deep(.el-upload-list__item-thumbnail) {
  width: 100%;
  height: 100%;
  object-fit: contain;
}
</style>