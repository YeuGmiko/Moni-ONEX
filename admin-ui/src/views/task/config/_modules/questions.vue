<script setup lang="ts">
import { h, onMounted, ref } from 'vue';
import type { DataTableColumns } from 'naive-ui';
import { NButton } from 'naive-ui';
import type { Question } from '@/service/api/question';
import { fetchTaskDailyQuestions } from '@/service/api/mask';
import CheckQuestion from '@/views/task/config/_modules/check-question.vue';

const loading = ref(false);
const showCheckQuestion = ref(false);
const questions = ref<Question[]>([]);
const currentQuestion = ref<Question | null>(null);

const tableColumns: DataTableColumns<Question> = [
  {
    key: 'no',
    title: 'No',
    render(_, index: number) {
      return `${index + 1}`;
    }
  },
  {
    key: 'moduleName',
    title: '模块'
  },
  {
    key: 'title',
    title: '题目'
  },
  {
    key: 'createTime',
    title: '创建时间'
  },
  {
    key: 'op',
    title: '操作',
    width: 100,
    render(row: Question) {
      return h(
        NButton,
        {
          type: 'primary',
          text: true,
          onClick() {
            currentQuestion.value = row;
            openCheckQuestion();
          }
        },
        '查看'
      );
    }
  }
];

async function fetchData() {
  loading.value = true;
  const { data, error } = await fetchTaskDailyQuestions();
  if (error) {
    loading.value = false;
    return;
  }
  questions.value = data;
  loading.value = false;
}
function openCheckQuestion() {
  showCheckQuestion.value = true;
}
onMounted(fetchData);
</script>

<template>
  <NCard title="每日题目列表" size="small">
    <NDataTable
      class="h-full"
      flex-height
      :data="questions"
      :columns="tableColumns"
      :loading="loading"
      remote
    ></NDataTable>
    <CheckQuestion v-model:show="showCheckQuestion" :question-id="currentQuestion?.id"></CheckQuestion>
  </NCard>
</template>

<style scoped></style>
