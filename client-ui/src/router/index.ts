import { createRouter, createWebHistory } from 'vue-router'
import type { App } from 'vue'
import { ROUTE_NAME, routes } from '@/router/routes'
import { useAuthStore } from '@/store/authStore'
import {storeToRefs} from 'pinia'

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach(async (to, from, next) => {
    const authStore = useAuthStore()
    const { setup } = authStore
    const { isLogin } = storeToRefs(authStore)
    /* 初始化用户授权信息，形参为true则表示无授权时跳转至登录页 */
    await setup(true)
    const { requireAuth } = to.meta

    if (isLogin.value) {
        /* 登录状态下对登录页面进行拦截 */
        if (to.name === ROUTE_NAME.authLogin) next({ name: ROUTE_NAME.home })
        else next()
    } else if (!requireAuth) {
        /* 对无需认证的路由放行 */
        next()
    } else {
        window?.$message?.error('请先登录')
        next({
            name: ROUTE_NAME.authLogin,
            query: {
                redirect: to.fullPath,
            },
        })
    }
})

export function setupRouter(app: App) {
    app.use(router)
}
