<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Module, QuestionOrder } from '@/server/api/types'
import { ROUTE_NAME } from '@/router/routes'
import ModuleTag from '@/components/common/module-tag.vue'
import { fetchModules } from '@/server/api/modules'
import { fetchQuestionOrders } from '@/server/api/questions'

const isModulesLoading = ref<boolean>(false)
const isQuestionOrdersLoading = ref<boolean>(false)
const modules = ref<Module[]>([])
const questions = ref<QuestionOrder[]>([])

const pagination = ref<{
  page: number
  size: number
  total: number
  pages: number
}>({
  page: 1,
  size: 20,
  total: 0,
  pages: 0,
})


async function fetchModulesData() {
  isModulesLoading.value = true
  try {
    const { data, error } = await fetchModules()
    if (error) return
    modules.value = data
    currentModule.value = data[0]
  } finally {
    isModulesLoading.value = false
  }
}

async function fetchQuestionOrdersData() {
  await handleModuleClick(currentModule.value)
}

const router = useRouter()
const currentModule = ref<Module | undefined>()

async function init() {
  await fetchModulesData()
  await fetchQuestionOrdersData()
}

async function handleModuleClick(module?: Module) {
  if (!module) return
  isQuestionOrdersLoading.value = true
  try {
    currentModule.value = module
    const { error, data } = await fetchQuestionOrders(module.id, pagination.value.page, pagination.value.size)
    if (error) return
    const { records, pages, total } = data
    pagination.value.pages = pages
    pagination.value.total = total
    questions.value = records
  } finally {
    isQuestionOrdersLoading.value = false
  }
}

function handleQuestionOrderClick(question: QuestionOrder) {
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
onMounted(init)
</script>

<template>
  <div class="flex-grow flex flex-col m-2 mx-auto max-w-[1640px] w-90%">
    <div class="flex flex-col">
      <h2 class="mb-0">模块</h2>
      <div v-if="isModulesLoading" class="flex-grow flex flex-col items-start">
        <Icon class="mt-2" name="ri-loader-2-line" :scale="2" animation="spin" speed="slow"></Icon>
        <p class="mt-1 mb-0 text-[18px]">模块加载中，请稍等...</p>
      </div>
      <NScrollbar v-else-if="modules.length > 0" x-scrollable>
        <div class="my-2 mx-1 flex gap-x-2 overflow-x-auto">
          <ModuleTag class="cursor-pointer" v-for="tag in modules" :label="tag.label" :theme-color="tag.bgColor" :active="tag.id === currentModule?.id" @click="handleModuleClick(tag)"></ModuleTag>
        </div>
      </NScrollbar>
      <div v-else class="flex-grow flex flex-col items-start h-full">
        <Icon class="mt-2" name="fc-delete-database" :scale="2"></Icon>
        <p class="mb-0">暂时没有模块呢，去喊老师添加一下吧。</p>
      </div>
    </div>
    <div v-show="!isModulesLoading" class="flex-grow flex flex-col h-full">
      <h2>题目列表</h2>
      <!--  loading -->
      <div v-if="isQuestionOrdersLoading" class="flex-grow flex flex-col justify-center items-center">
        <Icon name="ri-loader-2-line" :scale="2" animation="spin" speed="slow"></Icon>
        <p class="mt-1 text-[18px]">题目加载中，请稍等...</p>
      </div>
      <!--  questions -->
      <div v-else-if="(currentModule?.questionCount ?? 0) > 0" class="flex-grow flex flex-col">
        <div class="grid grid-cols-2 gap-x-2 gap-y-3">
          <div class="question-tag" v-for="question in questions" :key="question.id" @click="handleQuestionOrderClick(question)"  :style="{ '--theme-color': currentModule?.bgColor }">
            <span class="color-block" :class="{
              right: question.accomplishStatus === 2,
              wrong: question.accomplishStatus === 1
            }"></span>
            <h3 class="inline-block pl-2 whitespace-nowrap text-ellipsis overflow-hidden">
              {{ question.title }}
            </h3>
          </div>
        </div>
        <div class="my-3 flex items-center gap-x-2">
          <NPagination
              v-model:page="pagination.page"
              v-model:page-size="pagination.size"
              v-model:default-page-count="pagination.pages"
              v-model:page-count="pagination.pages"
              size="large"
              :default-page-size="20"
              :page-sizes="[10, 20, 30, 50, 100]"
              show-quick-jumper
              show-size-picker
              @update:page="fetchQuestionOrdersData"
              @update:page-size="fetchQuestionOrdersData"
          >
            <template #goto>跳转</template>
          </NPagination>
          <span>总数: {{ currentModule?.questionCount }}</span>
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
