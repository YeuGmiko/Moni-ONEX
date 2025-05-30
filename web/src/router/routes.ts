import type { RouteRecordRaw } from 'vue-router'

export const ROUTE_NAME = {
    authLogin: 'auth_login',
    layout: 'layout',
    home: 'home',
    modules: 'modules',
    task: 'task'
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
                component: () => import('@/views/question/question.vue'),
                meta: {
                    requireAuth: true
                }
            },
            {
                path: 'task/:id',
                name: ROUTE_NAME.task,
                component: () => import('@/views/task/task.vue'),
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
            }
        ]
    },
]
