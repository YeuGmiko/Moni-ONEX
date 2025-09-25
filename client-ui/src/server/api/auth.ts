import { request } from '@/server'
import type { LoginUser, UserInfo } from '@/server/api/types'

export interface LoginBody {
    userName: string
    password: string
    rememberMe: boolean
}

export interface RegisterBody {
    name: string
    userName: string
    password: string
}

export interface UpdatePasswordBody {
    oldPassword: string
    newPassword: string
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

export function fetchRegisterUser(body: RegisterBody) {
    return request<LoginUser>({
        method: 'POST',
        url: '/auth/register',
        data: body
    })
}

export function fetchUserInfo() {
    return request<UserInfo>({
        url: '/auth/info',
        method: 'GET',
    })
}

export function updatePassword(body: UpdatePasswordBody) {
    return request({
        url: '/auth/password',
        method: 'PUT',
        data: body
    })
}
