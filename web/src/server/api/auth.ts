import { request } from '@/server'
import type { LoginUser, UserInfo } from '@/server/api/types'

export interface LoginBody {
    userName: string
    password: string
    rememberMe: boolean
}

export function fetchLogout() {
    return request({
        url: '/auth/logout',
        method: 'GET'
    })
}

export function fetchLogin(body: LoginBody) {
    return request<LoginUser>({
        method: 'POST',
        url: '/auth/login',
        data: body
    })
}

export function fetchUserInfo() {
    return request<UserInfo>({
        url: '/auth/info',
        method: 'GET',
    })
}
