import type { Response } from '@/server'
import {createFlatRequest} from '@/server'
import {getServiceBaseURL} from '@/utils/sercice'

const isHttpProxy = import.meta.env.VITE_HTTP_PROXY === 'Y'
const { baseURL } = getServiceBaseURL(import.meta.env as Env.ImportMeta, isHttpProxy)
export const request = createFlatRequest<Response>({
    baseURL
}, {
    onRequest(config) {
        const Authorization = getAuthorization()
        Object.assign(config, {
            headers: {
                Authorization
            }
        })
        return config
    },
    transformResponse(response) {
        // return new Promise(resolve => {
        //     const delay = Math.floor(Math.random() * 1500) + 500
        //     setTimeout(() => {
        //         resolve(response.data.data)
        //     }, delay)
        // })
        return response.data.data
    },
    /* important */
    isBackendSuccess(response) {
        const { code } = response.data
        const codes: number[] = import.meta.env.VITE_SERVICE_SUCCESS_CODES.toString().split(',').map((code: string) => Number.parseInt(code))
        return codes.includes(code)
    },
    onError(error) {
        if (error.response) {
            const { msg: errMsg } = error.response.data
            window?.$message?.error(errMsg)
        }
    }
})

export function getAuthorization() {
    const tokenBearer = import.meta.env.VITE_SERVICE_TOKEN_BEARER || ''
    const tokenValue = window.localStorage.getItem('token-value')
    return tokenValue ? `${tokenBearer} ${tokenValue}`.trim() : null
}
export * from './request'
export * from './type'
