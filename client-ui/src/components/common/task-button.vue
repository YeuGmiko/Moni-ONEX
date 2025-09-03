<script setup lang="ts">
import {computed, onMounted} from 'vue'
import { useRouter } from 'vue-router'
import {ROUTE_NAME} from '@/router/routes'
import { useMessage } from 'naive-ui'
import { useTaskStore } from '@/store'
import {storeToRefs} from 'pinia'

const router = useRouter()
const message = useMessage()
const taskStore = useTaskStore()
const { getData: getTasks } = taskStore
const { data: taskData, isLoading: isTaskLoading } = storeToRefs(taskStore)
const process = computed<{ total: number, completed: number }>(() => ({
  total: taskData.value.length,
  completed: taskData.value.filter(task => task.accomplishStatus !== 0).length
}))
const isAccomplished = computed<boolean>(() => !isTaskLoading.value.data && taskData.value.every(task => task.accomplishStatus !== 0))

async function toTaskPage() {
  const id = taskData.value[0]?.id
  if (!id) {
    message.info('当前没有任务了哦！')
    return
  }
  await router.push({ name: ROUTE_NAME.task, params: { id } })
}

onMounted(getTasks)
</script>

<template>
  <NButton size="small" :color="isAccomplished ? 'green' : 'red'" ghost :loading="isTaskLoading.data" @click.prevent="toTaskPage">
    <template #icon>
      <Icon v-if="isAccomplished" name="oi-tasklist"></Icon>
      <Icon v-else name="ri-notification-badge-line"></Icon>
    </template>
    <template #default>
      {{ `${process.completed} / ${process.total}` }}
    </template>
  </NButton>
</template>
