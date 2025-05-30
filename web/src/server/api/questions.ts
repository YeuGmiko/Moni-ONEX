import {request} from '@/server'
import {PostQuestionOption, Question, QuestionAnswer, QuestionOrder, SubmitQuestionOption} from '@/server/api/types'

export function fetchQuestions(moduleId: string) {
    return request<Question[]>({
        url: `/modules/${moduleId}/questions`,
        method: 'GET'
    })
}
export function fetchQuestion(id: string) {
    return request<Question>({
        url: `/modules/questions/${id}`,
        method: 'GET'
    })
}
export function fetchQuestionOrders(moduleId: string) {
    return request<QuestionOrder[]>({
        url: `/modules/${moduleId}/questions/orders`,
        method: 'GET'
    })
}
export function postQuestionSubmits(questionId: string, options: PostQuestionOption[]) {
    return request<QuestionAnswer[]>({
        url: `/modules/questions/${questionId}/submits`,
        method: 'POST',
        data: options
    })
}

export function fetchQuestionSubmits(questionId: string) {
    return request<SubmitQuestionOption[]>({
        url: `/modules/questions/${questionId}/submits`,
        method: 'GET'
    })
}

export function fetchQuestionAnswers(questionId: string) {
    return request<QuestionAnswer[]>({
        url: `/modules/questions/${questionId}/answers`,
        method: 'GET'
    })
}
