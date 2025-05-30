import { defineStore } from 'pinia'
import {watch, reactive, readonly, ref, computed} from 'vue'
import {
    PostTaskOption,
    Task,
    TaskAnswer,
} from '@/server/api/types'
import {fetchTask, fetchTaskAnswer, fetchTasks, postTaskSubmits} from '@/server/api/task'

export const useTaskStore = defineStore('task', () => {
    const data = ref<Task[]>([])
    const current = ref<Task | null>(null)
    const nextTarget = ref<Task | null>(null)
    const isSubmit = computed<boolean>(() => {
        return current.value && current.value?.accomplishStatus !== 0
    })
    const answers = ref<TaskAnswer[]>([])
    const isLoading = reactive<{
        data: boolean
        target: boolean
    }>({
        data: false,
        target: false
    })

    async function getData() {
        isLoading.data = true
        const { data: responseData, error } = await fetchTasks()
        isLoading.data = false
        if (error || !data) return Promise.reject(error ?? new Error('no data'))
        data.value = responseData
    }

    async function getTarget(id: string) {
        if (!id) return Promise.reject(new Error('id is required'))
        isLoading.target = true
        const { data: responseData, error } = await fetchTask(id)
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

    async function submit(options: PostTaskOption[]) {
        if (!current.value || !current.value.id) return
        const { error } = await postTaskSubmits(current.value.id, options)
        if (error) return Promise.reject(error)
        await getTarget(current.value.id)
    }

    async function getAnswers(id: string) {
        const { data, error } = await fetchTaskAnswer(id)
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
