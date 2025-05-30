import {defineStore} from 'pinia'
import {reactive, readonly, ref} from 'vue'
import {Module} from '@/server/api/types'
import {fetchModule, fetchModules} from '@/server/api/modules'

export const useModuleStore = defineStore('module', () => {
    const isLoading = reactive<{
        data: boolean
        target: boolean
    }>({ data: false, target: false })
    const data = ref<Module[]>([])
    const current = ref<Module | null>(null)

    async function getData() {
        isLoading.data = true
        const { data: responseData, error } = await fetchModules()
        isLoading.data = false
        if (error || !responseData) return Promise.reject(error ?? new Error( 'no data'))
        data.value = responseData
    }

    async function getTarget(id: string) {
        if (!id) return Promise.reject(new Error('id is required'))
        else if (current.value && current.value.id == id)
        isLoading.target = true
        const { data: responseData, error } = await fetchModule(id)
        isLoading.target = false
        if (error || !responseData) return Promise.reject(error ?? new Error( 'no data'))
        current.value = responseData
    }


    return {
        data: readonly(data),
        current: readonly(current),
        isLoading,
        getData,
        getTarget
    }
})
