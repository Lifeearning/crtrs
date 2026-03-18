import axios from 'axios'
import router from '@/router'

const service = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 30000
})

// 请求拦截器
service.interceptors.request.use(config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')

    // 如果存在token则添加到请求头
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    // 如果是登录请求/注册请求，不携带token
    if (config.url.includes('/user/login') || config.url.includes('/user/register')) {
        delete config.headers.Authorization
    }

    return config
}, error => {
    return Promise.reject(error)
})

// 响应拦截器
service.interceptors.response.use(
    response => {
        // 对响应数据做统一处理
        return response.data
    },
    error => {
        // 处理网络错误和响应错误
        if (!error.response) {
            alert('网络连接异常，请检查网络')
            return Promise.reject(error)
        }

        const { status, data } = error.response

        // 401处理逻辑
        if (status === 401) {
            alert('登录已过期，请重新登录')

            // 清除本地认证信息
            localStorage.removeItem('token')

            // 带重定向参数的跳转
            router.push({
                path: '/login',
            })

            // 终止Promise链
            return new Promise(() => { })
        }

        // 其他错误处理
        if (status >= 500) {
            alert('服务器内部错误')
        } else if (status === 403) {
            alert('没有操作权限')
        } else {
            alert('请求错误')
        }

        return Promise.reject(error)
    }
)

export default service