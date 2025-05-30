import {request} from '@/server'
import {PostTaskOption, Task, TaskAnswer, TaskOrder, TaskSubmit} from '@/server/api/types'

export function fetchTasks() {
    return request<Task[]>({
        url: '/task/orders',
        method: 'GET'
    })
}
export function fetchTask(taskId: string) {
    return request<Task>({
        url: `/task/${taskId}`,
        method: 'GET'
    })
}

export function postTaskSubmits(taskId: string, options: PostTaskOption[]) {
    return request({
        url: `/task/${taskId}/submits`,
        method: 'POST',
        data: options
    })
}

export function fetchTaskSubmits(taskId: string) {
    return request<TaskSubmit[]>({
        url: `task/${taskId}/submits`,
        method: 'GET'
    })
}

export function fetchTaskAnswer(taskId: string) {
    return request<TaskAnswer[]>({
        url: `/task/${taskId}/answers`,
        method: 'GET'
    })
}
