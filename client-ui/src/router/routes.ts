import type { RouteRecordRaw } from 'vue-router'

export const ROUTE_NAME = {
    authLogin: 'auth_login',
    authRegister: 'auth_register',
    layout: 'layout',
    home: 'home',
    modules: 'modules',
    userProfile: 'user_profile'
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
                path: 'profile',
                name: ROUTE_NAME.userProfile,
                component: () => import('@/views/userProfile/index.vue'),
            },
            {
                path: 'modules/:id',
                name: ROUTE_NAME.modules,
                component: () => import('@/views/question/question.vue'),
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
                component: () => import('@/views/auth/modules/login.vue')
            },
            {
                path: '/register',
                name: ROUTE_NAME.authRegister,
                component: () => import('@/views/auth/modules/register.vue')
            }
        ]
    },
]
