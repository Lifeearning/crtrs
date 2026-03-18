<template>
    <div class="login-container">
        <el-card class="login-box">
            <h2 class="login-title">用户登录</h2>
            <el-form 
                :model="form" 
                :rules="rules" 
                ref="loginForm"
                label-width="80px"
                @keyup.enter="handleLogin"
            >
                <el-form-item label="用户名" prop="u_name">
                    <el-input v-model="form.u_name" placeholder="请输入用户名"></el-input>
                </el-form-item>

                <el-form-item label="密码" prop="u_pwd">
                    <el-input 
                        v-model="form.u_pwd" 
                        type="password" 
                        placeholder="请输入密码"
                        show-password
                    ></el-input>
                </el-form-item>

                <el-form-item>
                    <el-button 
                        type="primary" 
                        class="login-btn" 
                        @click="handleLogin"
                        round
                    >
                        立即登录
                    </el-button>
                </el-form-item>
                <div class="register-link">
                    <el-link type="primary" @click="toRegister">还没有账号？立即注册</el-link>
                </div>
            </el-form>
        </el-card>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import service from '@/api/index'
import { useRouter } from 'vue-router';

const router = useRouter()
// 表单数据
const form = reactive({
    u_name: '',
    u_pwd: '',
})

// 表单验证规则
const rules = reactive({
    u_name: [
        { required: true, message: '请输入账号', trigger: 'blur' },
        { min: 1, max: 12, message: '长度在 1 到 12 个字符', trigger: 'blur' }
    ],
    u_pwd: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 18, message: '长度在 6 到 18 个字符', trigger: 'blur' }
    ]
})

// 表单引用
const loginForm = ref(null)

// 登录处理
const handleLogin = () => {
    loginForm.value.validate(async valid => {
        if (valid) {
            try{
                let res = await service.post('/user/login', form)
                console.log(res);
                let code = res.code
                
                if(code === 0){     
                    // 登录成功，将用户基本信息存储在localStorage中，跳转首页
                    localStorage.setItem('uid', res.data.uid)
                    localStorage.setItem('ucount', res.data.ucount)
                    localStorage.setItem('upho', res.data.upho)
                    localStorage.setItem('token', res.data.token)
                    localStorage.setItem('balance', res.data.balance)
                    localStorage.setItem('avatar', res.data.avatar)
                    router.push('/index')
                }else if(code === 1){
                    alert('用户名或密码错误')
                }
            } catch {
                alert('网络错误')
            }
        } else {
            alert('格式错误！')
            return false
        }
    })
}

const toRegister = () => {
    router.push('/register')
}
</script>

<style scoped>
.login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    background-color: #f5f7fa;
}

.login-box {
    width: 400px;
    padding: 30px 20px;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-title {
    text-align: center;
    margin-bottom: 30px;
    color: #409EFF;
}

.forgot-password {
    float: right;
}

.login-btn {
    width: 100%;
    margin-top: 10px;
}

.register-btn {
    margin-left: 10%;
}

.register-link{
    margin-left: 20px;
    text-align: center;
    margin-top: 15px;
    color: #86909c;
    font-size: 14px;
}

/* 动画效果 */
.login-box {
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