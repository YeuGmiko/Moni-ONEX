<script setup lang="ts">
import type { DataTableColumns } from 'naive-ui';
import { NButton, NPopconfirm, NSpace, useMessage } from 'naive-ui';
import { h, ref, toRefs, watch } from 'vue';
import { useTable } from '@/hooks/common/table';
import AddQuestion from '@/views/questions/_modules/AddQuestion.vue';
import type { Module, Question } from '@/service/api/question';
import { deleteQuestion, fetchQuestions } from '@/service/api/question';
import UpdateQuestion from '@/views/questions/_modules/UpdateQuestion.vue';
import UploadQuestion from '@/views/questions/_modules/UploadQuestion.vue';

interface Props {
  module: Module;
}
const props = defineProps<Props>();
const { module } = toRefs(props);
const message = useMessage();
const showAddQuestion = ref<boolean>(false);
const showUploadQuestion = ref<boolean>(false);
const showUpdateQuestion = ref<boolean>(false);
const currentQuestion = ref<Question | null>(null);

async function fetchData() {
  const { data, error } = await fetchQuestions(module.value.id);
  if (error) throw error;
  return {
    data: {
      records: data,
      page: 1,
      size: 1000,
      total: 1000
    }
  };
}
const tableColumns: DataTableColumns<Question> = [
  {
    key: 'no',
    title: 'No',
    render(_, index: number) {
      return `${index + 1}`;
    }
  },
  {
    key: 'title',
    title: '题目'
  },
  {
    key: 'displayOrder',
    title: '排序'
  },
  {
    key: 'createTime',
    title: '创建时间'
  },
  {
    key: 'op',
    title: '操作',
    render(row: Question) {
      const deleteEl = h(
        NPopconfirm,
        {
          async onPositiveClick() {
            const { error } = await deleteQuestion(row.id);
            if (error) return;
            message.success('删除成功');
            await refresh();
          }
        },
        {
          trigger: h(
            NButton,
            {
              type: 'error',
              text: true
            },
            '删除'
          ),
          default: '请确定后删除'
        }
      );
      const editEl = h(
        NButton,
        {
          type: 'primary',
          text: true,
          onClick() {
            currentQuestion.value = row;
            openUpdateQuestion();
          }
        },
        '编辑'
      );
      return h(NSpace, null, [deleteEl, editEl]);
    }
  }
];
const { columns, data, loading, getData } = useTable<Question>({
  columns: () => tableColumns,
  apiFn: fetchData,
  immediate: false,
  apiParams: {
    page: 1,
    size: 1000,
    moduleId: module.value?.id || ''
  }
});

function openAddQuestion() {
  showAddQuestion.value = true;
}
function openUploadQuestion() {
  showUploadQuestion.value = true;
}
function openUpdateQuestion() {
  showUpdateQuestion.value = true;
}

async function refresh() {
  await getData();
}

function close() {
  showAddQuestion.value = false;
  showUpdateQuestion.value = false;
  showUploadQuestion.value = false;
}

watch(
  module,
  newValue => {
    if (!newValue) return;
    getData();
  },
  {
    immediate: true
  }
);
</script>

<template>
  <div class="h-full flex flex-col gap-y-2 p-1">
    <div class="flex justify-between gap-x-2">
      <NTag type="success" class="self-start">{{ module?.name }}</NTag>
      <div>
        <NButton type="primary" size="small" ghost @click="openAddQuestion">添加题目</NButton>
        <NButton class="ml-2" type="primary" size="small" @click="openUploadQuestion">批量导入</NButton>
      </div>
    </div>
    <NDataTable
      class="flex-grow"
      pagination
      flex-height
      :columns="columns"
      :data="data"
      :loading="loading"
    ></NDataTable>
    <UpdateQuestion
      v-model:show="showUpdateQuestion"
      :question-id="currentQuestion?.id"
      @close="close"
      @success="refresh"
    />
    <AddQuestion
      v-model:show="showAddQuestion"
      :default-order="data.length"
      :module-id="module.id"
      @close="close"
      @success="refresh"
    ></AddQuestion>
    <UploadQuestion
      v-model:show="showUploadQuestion"
      :module-id="module.id"
      @close="close"
      @success="refresh"
    ></UploadQuestion>
  </div>
</template>

<style scoped></style>
