import { defineStore } from 'pinia'
import { computed, readonly, ref } from 'vue'
import { useRouter } from 'vue-router'
import type { UserInfo } from '@/server/api/types'
import {fetchLogin, fetchLogout, fetchUserInfo, LoginBody} from '@/server/api/auth'
import {AxiosError} from 'axios'
import type {Response} from '@/server'
import {ROUTE_NAME} from '@/router/routes'

export const useAuthStore = defineStore('auth', () => {
    const router = useRouter()

    const loginUser = ref<UserInfo | undefined>(undefined)
    const tokenValue = ref<string>('')
    const isLogin = computed(() => Boolean(tokenValue.value))


    async function login(loginBody: LoginBody): Promise<AxiosError<Response<any>> | undefined> {
        const { data, error } = await fetchLogin(loginBody)
        if (error || !data) return error
        const { token } = data
        tokenValue.value = token
        window?.localStorage.setItem('token-value', token)
        return await getLoginUser()
    }

    async function getLoginUser(indirect: boolean = false): Promise<AxiosError<Response<any>> | undefined> {
        if (!isLogin.value) return new AxiosError('无token凭证')
        const { data, error } = await fetchUserInfo()
        if (error || !data) {
            /* login failed, to logout */
            logout()
            if (indirect) await router.replace({name: ROUTE_NAME.authLogin})
            return error
        }
        loginUser.value = data
        return undefined
    }

    function setTokenValue(token: string) {
        setLocalToken(token)
        return getLoginUser()
    }

    function logout(sendRequest?: boolean) {
        if (sendRequest) fetchLogout()
        setLocalToken()
        reset()
    }

    function reset() {
        tokenValue.value = ''
        loginUser.value = undefined
    }

    async function setup(direct?: boolean) {
        if (isLogin.value) return
        tokenValue.value = getLocalToken() || ''
        await getLoginUser(direct)
    }

    function getLocalToken(): string {
        return window?.localStorage?.getItem('token-value') || ''
    }
    function setLocalToken(token?: string) {
        tokenValue.value = token || ''
        if (!token) window?.localStorage?.removeItem('token-value')
        else window?.localStorage?.setItem('token-value', token)
    }

    return {
        loginUser: readonly(loginUser),
        login,
        isLogin,
        setTokenValue,
        logout,
        setup
    }
})
