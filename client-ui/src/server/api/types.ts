export interface LoginUser {
    userId: string
    loginTime: string
    token: string
    rememberMe: boolean
}
export interface UserInfo {
    userId: string
    name: string
    userName: string
    roles: string[]
}
export interface OrderProperties {
    id: string
    title: string
    // 题目完成状态：0未提交，1部分/全部错误，2全部正确
    accomplishStatus: number
    displayOrder: number
}

/* modules */
export interface Module {
    id: string
    label: string
    name: string
    bgColor: string
    displayOrder: string
    questionCount: number
    questions: OrderProperties[]
    remark: string
}
/* questions */
export interface Question {
    id: string
    title: string
    content: string
    displayOrder: number
    optionsLen: number
    moduleId: string
    moduleName: string
    moduleLabel: string
    themeColor: string
    // 题目完成状态：0未提交，1部分/全部错误，2全部正确
    accomplishStatus: number
    userSubmits: SubmitQuestionOption[]
    createTime: string
}
export interface SubmitQuestionOption {
    id: string
    orderNo: number
    userAnswer: string
}
export interface QuestionAnswer {
    id: string
    orderNo: number
    answer: string
}
export type QuestionOrder = OrderProperties
export interface PostQuestionOption {
    orderNo: number
    answer: string
}
