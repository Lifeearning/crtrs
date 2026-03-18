<template>
  <div class="register-container">
    <!-- 背景装饰 -->
    <div class="background-decoration"></div>
    
    <!-- 注册卡片 -->
    <el-card class="register-card">
      <div class="register-header">
        <h2>账号注册</h2>
        <p>创建新账号，开启全新体验</p>
      </div>
      
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        label-width="80px"
      >
        <!-- 用户名 -->
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            clearable
          />
        </el-form-item>
        
        <!-- 手机号 -->
        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入手机号"
            clearable
            type="tel"
          />
        </el-form-item>
        
        <!-- 密码 -->
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        
        <!-- 确认密码 -->
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            placeholder="请再次输入密码"
            show-password
          />
        </el-form-item>
        
        <!-- 注册按钮 -->
        <el-form-item class="submit-btn">
          <el-button 
            type="primary" 
            :loading="isLoading" 
            @click="handleRegister"
            round
            class="w-full"
          >
            注册
          </el-button>
        </el-form-item>
        
        <!-- 已有账号 -->
        <div class="login-link">
          <el-link type="primary" @click="toLogin">已有账号？立即登录</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import service from '@/api';

// 路由实例
const router = useRouter();

// 表单引用
const registerFormRef = ref(null);

// 加载状态
const isLoading = ref(false);

// 注册表单数据
const registerForm = reactive({
  username: '',
  phone: '',
  password: '',
  confirmPassword: '',
});

// 提交的表单数据
const submitData = reactive({
  username: '',
  phone: '',
  password: '',
});

// 表单验证规则
const registerRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { 
      pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,16}$/, 
      message: '密码需含大小写、数字和特殊字符，8-16位', 
      trigger: 'blur' 
    }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ],
});

// 处理注册
const handleRegister = async () => {
  // 表单验证
  const valid = await registerFormRef.value.validate();
  if (!valid) return;
  
  isLoading.value = true;
  try {
    submitData.username = registerForm.username;
    submitData.phone = registerForm.phone;
    submitData.password = registerForm.password;
    // 发送注册请求
    const res = await service.post('/user/register', submitData);
    if(res.code===1){
      registerForm.username = '';
      registerForm.phone = '';
      registerForm.password = '';
      registerForm.confirmPassword = '';
      alert(res.message+'，请重新注册');
      return;
    }else{
          alert('注册成功，即将跳转到登录页');
          // 注册成功后跳转到登录页
          setTimeout(() => {
            router.push('/login');
          }, 500);
        }
  } catch (error) {
    alert('注册失败，请稍后重试');
  } finally {
    isLoading.value = false;
  }
};

// 跳转到登录页
const toLogin = () => {
  router.push('/login');
};
</script>

<style scoped>

.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  position: relative;
  background-color: #f5f7fa;
}

.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: 
    radial-gradient(circle at 20% 30%, rgba(64, 158, 255, 0.1) 0%, transparent 40%),
    radial-gradient(circle at 80% 70%, rgba(64, 158, 255, 0.1) 0%, transparent 40%);
  z-index: 0;
}

.register-card {
  width: 100%;
  max-width: 420px;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  position: relative;
  z-index: 1;
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
  font-weight: 600;
  color: #1d2129;
}

.register-header p {
  margin: 0;
  color: #86909c;
}

.register-form {
  margin-top: 20px;
}

.code-btn {
  width: 100%;
  padding: 10px 0;
  border: 1px solid #e5e6eb;
  border-radius: 4px;
}

.agreement-item {
  margin-left: 80px;
  font-size: 12px;
}

.submit-btn {
  margin-top: 10px;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  color: #86909c;
  font-size: 14px;
}

.w-full {
  width: 100%;
}

/* 动画效果 */
.register-card {
  animation: fadeIn 0.5s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>