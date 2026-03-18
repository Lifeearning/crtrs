<template>
  <div class="update-info-container">
    <el-card class="box-card" style="max-width: 700px; margin: 20px auto 50px;">
      <template #header>
        <div class="card-header">
          <span>修改个人信息</span>
        </div>
      </template>
      <el-form
        ref="formRef"
        :model="formData"
        :rules="registerRules"
        label-width="110px"
        size="large"
        class="user-form"
      >
        <!-- 头像上传 -->
        <el-form-item label="头像">
          <div class="avatar-upload-wrapper">
            <el-upload
              class="avatar-uploader"
              action="#"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :http-request="handleAvatarUpload"
            >
              <img v-if="formData.avatar" :src="formData.avatar" class="avatar-preview" />
              <div v-else class="avatar-placeholder">
                <span class="avatar-plus">+</span>
                <div class="avatar-text">点击上传头像</div>
              </div>
            </el-upload>
            <div class="avatar-tip">仅支持图片，大小不超过2MB</div>
          </div>
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input 
            v-model="formData.phone" 
            placeholder="不修改请留空" 
            style="width: 450px;"
          />
        </el-form-item>
        <el-form-item label="新密码" prop="password">
          <el-input
            v-model="formData.password"
            type="password"
            placeholder="不修改请留空（8-16位，含大小写、数字、特殊符号）"
            show-password
            style="width: 450px;"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="formData.confirmPassword"
            type="password"
            placeholder="修改密码时请输入"
            show-password
            style="width: 450px;"
          />
        </el-form-item>
        <el-form-item class="btn-group">
          <el-button type="primary" @click="handleSubmit" size="large" :loading="submitLoading">保存修改</el-button>
          <el-button @click="handleBack" size="large">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>

  <!-- 自定义提示框 -->
  <div class="custom-toast" :class="toastShow ? 'show' : ''" :style="toastStyle">
    {{ toastText }}
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import service from '@/api'

const router = useRouter()
const formRef = ref(null)
const avatarFile = ref(null)
const submitLoading = ref(false)

const formData = reactive({
  avatar: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

// 自定义提示框
const toastText = ref('')
const toastShow = ref(false)
const toastStyle = ref('')

function showToast(msg, type = 'success') {
  toastText.value = msg
  toastShow.value = true
  if (type === 'error') {
    toastStyle.value = 'background:#f56c6c;color:#fff'
  } else if (type === 'warning') {
    toastStyle.value = 'background:#e6a23c;color:#fff'
  } else {
    toastStyle.value = 'background:#409eff;color:#fff'
  }
  setTimeout(() => {
    toastShow.value = false
  }, 2600)
}

// 🔥 修复后的完整校验规则
const registerRules = reactive({
  phone: [
    {
      required: false,
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入正确的11位手机号码',
      trigger: ['blur', 'change']
    }
  ],
  password: [
    {
      required: false,
      pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,16}$/,
      message: '密码必须8-16位，包含大小写、数字、特殊符号',
      trigger: ['blur', 'change']
    }
  ],
  confirmPassword: [
    {
      validator: (rule, value, callback) => {
        if (formData.password) {
          if (!value) {
            return callback(new Error('请输入确认密码'))
          }
          if (value !== formData.password) {
            return callback(new Error('两次输入的密码不一致'))
          }
        } else if (value) {
          return callback(new Error('未设置新密码，无需填写确认密码'))
        }
        callback()
      },
      trigger: ['blur', 'change']
    }
  ]
})

// 头像上传校验
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    showToast('仅支持上传图片格式文件', 'error')
    return false
  }
  if (!isLt2M) {
    showToast('图片大小不能超过2MB', 'error')
    return false
  }

  avatarFile.value = file
  formData.avatar = URL.createObjectURL(file)
  showToast('头像已加载，保存后生效', 'success')
  return false
}

const handleAvatarUpload = () => {}

// 提交保存
const handleSubmit = async () => {
  try {
    await formRef.value.validate()

    if (!avatarFile.value && !formData.phone && !formData.password) {
      showToast('请至少修改一项信息后保存', 'warning')
      return
    }

    const postData = new FormData()
    if (avatarFile.value) postData.append('file', avatarFile.value)
    if (formData.phone) postData.append('phone', formData.phone)
    if (formData.password) postData.append('password', formData.password)

    submitLoading.value = true
    showToast('正在保存您的修改...', 'success')

    const res = await service.post('/user/update', postData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })

    if (res.code === 0) {
      showToast('信息修改成功，即将重新登录', 'success')
      localStorage.clear()
      setTimeout(() => {
        router.push('/login')
      }, 1800)
    } else {
      showToast(res.msg || '保存失败，请稍后重试', 'error')
    }
  } catch (err) {
    showToast('信息填写有误，请检查后重试', 'error')
  } finally {
    submitLoading.value = false
  }
}

const handleBack = () => {
  router.back()
}
</script>

<style scoped>
.update-info-container {
  padding: 10px 20px 30px;
  min-height: calc(80vh - 60px);
  display: flex;
  justify-content: center;
}
.box-card {
  border-radius: 10px;
  box-shadow: 0 3px 15px rgba(0,0,0,0.06);
  padding: 30px;
  width: 100%;
}
.card-header {
  font-weight: 700;
  font-size: 20px;
  color: #303133;
  margin-bottom: 15px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}
.avatar-upload-wrapper {
  display: flex;
  align-items: center;
  gap: 15px;
}
.avatar-uploader {
  width: 120px;
  height: 120px;
  border: 1px dashed #dcdcdc;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: border-color 0.2s;
}
.avatar-uploader:hover {
  border-color: #409eff;
}
.avatar-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}
.avatar-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #909399;
}
.avatar-plus {
  font-size: 30px;
  font-weight: 300;
  margin-bottom: 8px;
}
.avatar-text {
  font-size: 13px;
}
.avatar-tip {
  font-size: 12px;
  color: #999;
}
.btn-group {
  margin-top: 15px;
}
</style>