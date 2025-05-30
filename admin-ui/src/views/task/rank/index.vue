<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import type { DataTableColumns } from 'naive-ui';
import type { TaskRank } from '@/service/api/mask';
import { fetchTaskDailyRank } from '@/service/api/mask';

interface TableRowData {
  rank: number;
  name: string;
  userName: string;
  submit: string;
  score: string;
  accuracy: string;
}

const loading = ref<boolean>(false);
const rankData = ref<TaskRank[]>([]);
const tableColumns: DataTableColumns = [
  {
    key: 'rank',
    title: '排名'
  },
  {
    key: 'name',
    title: '姓名'
  },
  {
    key: 'userName',
    title: '用户名'
  },
  {
    key: 'submit',
    title: '提交'
  },
  {
    key: 'score',
    title: '综合分'
  },
  {
    key: 'accuracy',
    title: '准确率'
  }
];
const tableData = computed<TableRowData[]>(() => {
  return rankData.value.map(data => {
    const { rank, name, userName, submitOptions, totalOptions, rightOptions, submitQuestion, totalQuestions } = data;
    return {
      rank,
      name,
      userName,
      submit: `${submitQuestion}/${totalQuestions}`,
      score: totalOptions !== 0 ? `${((rightOptions / totalOptions) * 100).toFixed(1)}` : '0',
      accuracy: submitOptions !== 0 ? `${((rightOptions / submitOptions) * 100).toFixed(2)}%` : '0%'
    };
  });
});

async function fetchData() {
  loading.value = true;
  await fetchRankData();
  loading.value = false;
}
async function fetchRankData() {
  const { data, error } = await fetchTaskDailyRank();
  if (error) return;
  rankData.value = data;
}
onMounted(fetchData);
</script>

<template>
  <div class="m-2 rounded-1 bg-white p-2">
    <NDataTable class="h-full" flex-height :columns="tableColumns" :data="tableData" :loading="loading"></NDataTable>
  </div>
</template>

<style scoped></style>
