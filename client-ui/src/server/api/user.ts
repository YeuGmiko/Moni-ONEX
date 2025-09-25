import {request} from '@/server'
export interface UpdateUserCustomInfo {
    name: string
}
export function fetchCurrentUser(body: UpdateUserCustomInfo) {
    return request({
        url: '/users/info',
        method: 'PUT',
        data: body
    })
}
