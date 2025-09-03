import {request} from '@/server'
import type {Module} from '@/server/api/types'

export function fetchModules() {
    return request<Module[]>({
        url: '/modules',
        method: 'GET',
    })
}


export function fetchModule(id: string) {
    return request<Module>({
        url: `/modules/${id}`,
        method: 'GET',
    })
}
