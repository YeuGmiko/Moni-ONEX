import { defineStore } from 'pinia'
import {watch, reactive, readonly, ref, computed} from 'vue'
import {PostQuestionOption, Question, QuestionAnswer} from '@/server/api/types'
import {fetchQuestion, fetchQuestionAnswers, fetchQuestions, postQuestionSubmits} from '@/server/api/questions'

export const useQuestionStore = defineStore('question', () => {
    const data = ref<Question[]>([])
    const current = ref<Question | null>(null)
    const nextTarget = ref<Question | null>(null)
    const isSubmit = computed<boolean>(() => {
        return current.value && current.value?.accomplishStatus !== 0
    })
    const answers = ref<QuestionAnswer[]>([])
    const isLoading = reactive<{
        data: boolean
        target: boolean
    }>({
        data: false,
        target: false
    })

    async function getData(id: string) {
        if (!id) return Promise.reject(new Error('id is required'))
        isLoading.data = true
        const { data: responseData, error } = await fetchQuestions(id)
        isLoading.data = false
        if (error || !data) return Promise.reject(error ?? new Error('no data'))
        data.value = responseData
    }

    async function getTarget(id: string) {
        if (!id) return Promise.reject(new Error('id is required'))
        isLoading.target = true
        const { data: responseData, error } = await fetchQuestion(id)
        isLoading.target = false
        if (error || !responseData) return Promise.reject(error ?? new Error('no data'))
        current.value = responseData
        await updateDataByCurrent()
    }

    async function getNext() {
        if (!current.value || !current.value.id || isLoading.data) return
        const currentTargetId = current.value.id
        const currentIndex = data.value.findIndex(item => item.id === currentTargetId)
        nextTarget.value = data.value[currentIndex + 1]
    }

    async function toNext() {
        if (!nextTarget.value) return Promise.reject(new Error('no next value'))
        current.value = nextTarget.value
        await getNext()
    }

    async function updateDataByCurrent() {
        if (!current.value || !current.value.id || isLoading.data) return
        const currentTargetId = current.value.id
        const targetIndex = data.value.findIndex(item => item.id === currentTargetId)
        if (targetIndex === -1) return
        data.value[targetIndex] = current.value
    }

    async function submit(options: PostQuestionOption[]) {
        if (!current.value || !current.value.id) return
        const { error } = await postQuestionSubmits(current.value.id, options)
        if (error) return Promise.reject(error)
        await getTarget(current.value.id)
    }

    async function getAnswers(id: string) {
        const { data, error } = await fetchQuestionAnswers(id)
        if (error || !data) return []
        return data
    }

    function init() {
        data.value = []
        current.value = null
        nextTarget.value = null
    }

    watch(current, getNext, { immediate: true })
    watch(isSubmit, async isSubmit => {
        if (!current.value || !current.value.id || !isSubmit) {
            answers.value = []
        } else {
            answers.value = await getAnswers(current.value.id)
        }
    }, { immediate: true })

    return {
        data,
        current: readonly(current),
        nextTarget: readonly(nextTarget),
        isLoading: readonly(isLoading),
        isSubmit: readonly(isSubmit),
        answers: readonly(answers),
        getData,
        getTarget,
        toNext,
        submit,
        init
    }
})
