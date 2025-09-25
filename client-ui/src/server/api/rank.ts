import { request } from '@/server'
import type {Pagination, RankInfo} from '@/server/api/types'

export function fetchRankList(current: number, size: number) {
    return request<Pagination<RankInfo>>({
        url: '/rank',
        method: 'GET',
        params: { current, size}
    })
}

export function fetchDailyRankLIst(current: number, size: number) {
    return request<Pagination<RankInfo>>({
        url: `/rank/${formatDate(new Date())}`,
        method: 'GET',
        params: { current, size }
    })
}

function formatDate(date: Date) {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}`
}
