<script setup lang="ts">
import 'md-editor-v3/lib/style.css'
import '@/assets/markdown-custom.css'
import type { FormInst, FormRules } from 'naive-ui'
import { MdPreview } from 'md-editor-v3'
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type {
  OrderProperties,
  PostTaskOption,
} from '@/server/api/types'
import { ROUTE_NAME } from '@/router/routes'

import {useTaskStore} from '@/store'
import { storeToRefs } from 'pinia'
import { copyText } from '@/utils/system'

const route = useRoute()
const router = useRouter()
const taskStore = useTaskStore()
const submitFormRef = ref<FormInst>()
const { getData: getTaskData, getTarget: getTask, toNext: toNextTask, submit: submitTask, init: initTask } = taskStore
const { data: taskData, isLoading: isTaskLoading, current: currentTask, nextTarget: nextQuestion, isSubmit, answers } = storeToRefs(taskStore)
const module = computed<{ label: string, themeColor: string }>(() => ({
  label: currentTask.value?.moduleLabel ?? '',
  themeColor: currentTask.value?.themeColor ?? '#c3c3c3'
}))
const model = ref<Record<string, PostTaskOption>>({})
const modelRules = computed<FormRules>(() => {
  return Object.entries(model.value).reduce((rules, current) => {
    const [key, option] = current
    function validator() {
      if (!option.answer?.trim()) return new Error(`选项${option.orderNo}不能为空`)
      else return true
    }
    return Object.assign(rules, {
      [key]: { require: true, validator, trigger: 'change' },
    })
  }, {})
})
const markdown = computed<string>(() => currentTask.value?.content ?? '')
const wrongAnswers = computed<Record<string, string>>(() => {
  return answers.value.reduce((result, current) => {
    const key = getSubmitOptionKey(current.orderNo)
    if (model.value[key].answer === current.answer) return result
    return Object.assign(result, {
      [key]: current.answer
    })
  }, {})
})

async function init() {
  if (submitFormRef.value) submitFormRef.value.restoreValidation()
  initTask()
  await getTaskData()
  await getTaskByQuery()
}

async function getTaskByQuery() {
  if (isTaskLoading.value.data || isTaskLoading.value.target) return
  const id = route.params?.id as string
  if (!id) return
  await getTask(id)
}

async function submit() {
  if (isSubmit.value) return
  const result = await validateForm()
  if (!result) return
  await submitTask(Array.from(Object.values(model.value)))
}

async function validateForm() {
  if (!submitFormRef.value) return false
  try {
    await submitFormRef.value.validate()
    return true
  } catch {
    return false
  }
}
function getSubmitOptionKey(order: number) {
  return `order-${order}`
}

async function routerToHome() {
  await router.push({ name: ROUTE_NAME.home })
}

async function handleOrderChoose(order: OrderProperties) {
  if (!order || !order.id) return
  await getTask(order.id)
}

watch(currentTask, async task => {
  if (!task || !task.id) return
  await router.push({
    name: ROUTE_NAME.task,
    params: { id: task.id }
  })
  if (isSubmit.value) return
  const { optionsLen } = task
  model.value = Array.from({ length: optionsLen }, (_, index) => {
    return {
      orderNo: index + 1,
      answer: ''
    }
  }).reduce((model, current) => {
    return Object.assign(model, {
      [getSubmitOptionKey(current.orderNo)]: current
    })
  }, {})
})
/* 页面更换 / 刷新的时候，触发题目信息获取*/
watch(route, getTaskByQuery)
/* 检测是否已经提交，若提交则重新渲染表单 */
watch(isSubmit, isSubmit => {
  if (!isSubmit || !currentTask.value) return
  const { userSubmits } = currentTask.value
  model.value = userSubmits.reduce((model, current) => {
    const { orderNo, userAnswer } = current
    return Object.assign(model, {
      [getSubmitOptionKey(current.orderNo)]: {
        orderNo,
        answer: userAnswer
      }
    })
  }, {})
}, { immediate: true })
onMounted(init)
</script>

<template>
  <div class="m-5 flex-grow flex gap-x-5 mx-auto max-w-[1640px] w-90%">
    <div class="flex-grow flex flex-col">
      <span class="router-back w-[max-content]" @click="routerToHome">
        <Icon class="icon" name="ri-arrow-go-back-fill"></Icon>
        <span class="text">返回首页</span>
      </span>
      <div v-if="isTaskLoading.target || isTaskLoading.data"
           class="flex-grow flex flex-col justify-center items-center">
        <Icon name="ri-loader-2-line" :scale="2" animation="spin" speed="slow"></Icon>
        <p class="mt-1 text-[18px]">题目加载中，请稍等...</p>
      </div>
      <!-- 题目内容 -->
      <template v-else-if="currentTask">
        <h2>{{ currentTask.title }}</h2>
        <MdPreview id="md-preview" :modelValue="markdown" noMermaid></MdPreview>
        <h2>答题</h2>
        <NForm ref="submitFormRef" v-model:model="model" :rules="modelRules" label-placement="left"
               :disabled="isSubmit">
          <NFormItem v-for="option in Object.values(model)" :key="option.orderNo"
                     :path="getSubmitOptionKey(option.orderNo)">
            <template #label>
              <span class="option-order">{{ option.orderNo }}</span>
            </template>
            <NInput class="max-w-[250px]" v-model:value="option.answer" :placeholder="`请填写选项${option.orderNo}`" @keydown.enter="submit"></NInput>
            <NScrollbar v-if="isSubmit" x-scrollable :size="2" content-class="py-1.5">
              <!-- 错误标签-->
              <span v-if="wrongAnswers[getSubmitOptionKey(option.orderNo)]"
                    class="inline-block flex-grow flex flex-gap-1 whitespace-nowrap">
                <span>正确答案(<code>{{ wrongAnswers[getSubmitOptionKey(option.orderNo)]?.length }}字符</code>)</span>
                <!-- 原谅我偷懒写style里面了 -->
                <span :class="['px-1', 'text-white', 'rounded-0.5', 'cursor-pointer']"
                      :style="{ backgroundColor: module?.themeColor }"
                      @click="copyText(wrongAnswers[getSubmitOptionKey(option.orderNo)])">{{ wrongAnswers[getSubmitOptionKey(option.orderNo)] }}</span>
              </span>
              <!-- 正确标签 -->
              <span v-else-if="isSubmit" class="ml-2 is-right-answer" :style="{
                '--theme-color': module?.themeColor
              }">正确</span>
            </NScrollbar>
          </NFormItem>
          <NFormItem>
            <div class="flex gap-x-2">
              <NButton v-if="!isSubmit" type="primary" :color="module?.themeColor" @click="submit">提交</NButton>
              <NButton v-if="nextQuestion" class="items-center" type="primary" :color="module?.themeColor" text
                       @click="toNextTask">下一题</NButton>
            </div>
          </NFormItem>
        </NForm>
      </template>
      <div v-else class="flex-grow flex flex-col justify-center items-center">
        <Icon name="fc-disclaimer" :scale="6" fill="#fffff"></Icon>
        <p class="text-xl">该题目不存在或不允许被查看</p>
      </div>
    </div>
    <div>
      <ModuleTag :theme-color="module?.themeColor" :label="module?.label"></ModuleTag>
      <QuestionOrders v-if="currentTask"
          :theme-color="module?.themeColor"  :current="currentTask" :orders="taskData" @choose="handleOrderChoose">
      </QuestionOrders>
    </div>
  </div>
</template>

<style scoped>
.is-right-answer {
  --theme-color: #cc7cee;
  padding: 2px 5px;
  color: white;
  border-radius: 2px;
  background-color: var(--theme-color);
}

.option-order {
  --theme-color: #cc7cee;
  --right-color: var(--theme-color);
  --wrong-color: color-mix(in srgb, var(--theme-color) 60%, red);

  margin: 0 5px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  line-height: 1;
  border: 2px solid var(--theme-color);
  background-color: white;
  color: var(--theme-color);
  border-radius: 26px;

  &.right {
    border: 2px solid var(--right-color);
    color: var(--right-color);
  }

  &.wrong {
    border: 2px solid var(--wrong-color);
    color: var(--wrong-color);
  }
}

.router-back {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;

  .icon {
    padding: 4px;
    border: 2px solid black;
    border-radius: 4px;
  }

  .text {
    font-size: 0.8rem;
    font-weight: bold;
  }
}
</style>
