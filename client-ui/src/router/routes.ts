import type { RouteRecordRaw } from 'vue-router'
import AuthLogin from '@/views/auth/modules/login.vue'
import ModuleQuestion from '@/views/question/question.vue'

export const ROUTE_NAME = {
    authLogin: 'auth_login',
    layout: 'layout',
    home: 'home',
    modules: 'modules'
}

export const routes: RouteRecordRaw[] = [
    {
        path: '',
        name: ROUTE_NAME.layout,
        component: () => import('@/layout/index.vue'),
        children: [
            {
                path: '',
                name: ROUTE_NAME.home,
                component: () => import('@/views/home/index.vue')
            },
            {
                path: 'modules/:id',
                name: ROUTE_NAME.modules,
                component: ModuleQuestion,
                meta: {
                    requireAuth: true
                }
            }
        ]
    },
    {
        path: '/auth',
        component: () => import('@/views/auth/index.vue'),
        children: [
            {
                path: '',
                name: ROUTE_NAME.authLogin,
                component: AuthLogin
            }
        ]
    },
]
