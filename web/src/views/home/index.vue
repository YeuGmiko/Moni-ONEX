<script setup lang="ts">
import {storeToRefs} from 'pinia'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {Module, Question} from '@/server/api/types'
import {ROUTE_NAME} from '@/router/routes'
import ModuleTag from '@/components/common/module-tag.vue'

import { useModuleStore, useQuestionStore } from '@/store'

const moduleStore = useModuleStore()
const questionStore = useQuestionStore()
const { getData: getModuleData } = moduleStore
const { getData: getQuestionData } = questionStore
const { data: moduleData, isLoading: moduleLoading } = storeToRefs(moduleStore)
const { data: questionData, isLoading: questionLoading } = storeToRefs(questionStore)


const router = useRouter()
const currentModule = ref<Module | undefined>()

async function refresh() {
  await getModuleData()
  const first = moduleData.value[0]
  if (!first || !first.id) return
  await handleModuleClick(first)
}

async function handleModuleClick(module?: Module) {
  if (!module) return
  currentModule.value = module
  await getQuestionData(module.id)
}
function handleQuestionOrderClick(question: Question) {
  if (!currentModule.value || !currentModule.value.id || !question?.id) return
  router.push({
    name: ROUTE_NAME.modules,
    params: {
      id: currentModule.value.id
    },
    query: {
      question: question.id
    }
  })
}
onMounted(refresh)
</script>

<template>
  <div class="flex-grow flex flex-col m-2 mx-auto max-w-[1640px] w-90%">
    <div class="flex flex-col">
      <h2 class="mb-0">模块</h2>
      <div v-if="moduleLoading.data" class="flex-grow flex flex-col items-start">
        <Icon class="mt-2" name="ri-loader-2-line" :scale="2" animation="spin" speed="slow"></Icon>
        <p class="mt-1 mb-0 text-[18px]">模块加载中，请稍等...</p>
      </div>
      <NScrollbar v-else-if="moduleData.length > 0" x-scrollable>
        <div class="my-2 mx-1 flex gap-x-2 overflow-x-auto">
          <ModuleTag class="cursor-pointer" v-for="tag in moduleData" :label="tag.label" :theme-color="tag.bgColor" :active="tag.id === currentModule?.id" @click="handleModuleClick(tag)"></ModuleTag>
        </div>
      </NScrollbar>
      <div v-else class="flex-grow flex flex-col items-start h-full">
        <Icon class="mt-2" name="fc-delete-database" :scale="2"></Icon>
        <p class="mb-0">暂时没有模块呢，去喊老师添加一下吧。</p>
      </div>
    </div>
    <div class="flex-grow flex flex-col">
      <h2>题目列表</h2>
      <div v-if="questionLoading.data || moduleLoading.data" class="flex-grow flex flex-col justify-center items-center">
        <Icon name="ri-loader-2-line" :scale="2" animation="spin" speed="slow"></Icon>
        <p class="mt-1 text-[18px]">题目加载中，请稍等...</p>
      </div>
      <div v-else-if="questionData.length > 0" class="grid grid-cols-2 gap-x-2 gap-y-3">
        <!-- Item -->
        <div class="question-tag" v-for="question in questionData" :key="question.id" @click="handleQuestionOrderClick(question)"  :style="{ '--theme-color': currentModule?.bgColor }">
          <span class="color-block" :class="{
            right: question.accomplishStatus === 2,
            wrong: question.accomplishStatus === 1
          }"></span>
          <h3 class="inline-block pl-2 whitespace-nowrap text-ellipsis overflow-hidden">
            {{ question.title }}
          </h3>
        </div>
      </div>
      <div v-else class="flex-grow flex flex-col justify-center items-center h-full">
        <Icon name="fc-delete-database" :scale="6"></Icon>
        <p>该模块暂时没有题目呢，去喊老师添加一下吧。</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.question-tag {
  --theme-color: gray;
  --default-color: gray;
  --wrong-color:  color-mix(in srgb, var(--theme-color) 60%, red);
  --right-color: var(--theme-color);

  display: flex;
  height: 60px;
  background-color: white;
  border-radius: 3px;
  box-shadow: 1px 1px 2px 1px rgba(0, 0, 0, 0.05);
  transition: all  300ms ease-out;
  cursor: pointer;

  &:hover {
    transform: translateY(-1px) translateX(-1px) scale(1.008);
    box-shadow: 1px 1px 10px 1px rgba(0, 0, 0, 0.1);
  }

  .color-block {
    min-width: 8px;
    background-color: var(--default-color);

    &.right {
      background-color: var(--right-color);
    }
    &.wrong {
      background-color: var(--wrong-color);
    }
  }
}
</style>
