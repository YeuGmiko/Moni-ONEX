import {createRouter, createWebHistory} from 'vue-router'
import type {App} from 'vue'
import {ROUTE_NAME, routes} from '@/router/routes'
import { useAuthStore } from '@/store/authStore'


const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach(async (to, from, next) => {
    await useAuthStore().setup(true)
    const { requireAuth } = to.meta
    if (!requireAuth) {
        next()
        return
    }
    const token = window?.localStorage.getItem('token-value')
    // 若含有token，暂时放行，等待authStore初始化完成后，token失效则会跳转至登录页
    if (token) next()
    else {
        window?.$message.error('请先登录')
        next({ name: ROUTE_NAME.authLogin })
    }
})

export function setupRouter(app: App) {
    app.use(router)
}
