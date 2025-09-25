<script setup lang="ts">
import { h, ref } from 'vue';
import type { DataTableColumns } from 'naive-ui';
import { NButton, NPopconfirm, NSpace, useMessage } from 'naive-ui';
import { useTable } from '@/hooks/common/table';
import QuestionDataTable from '@/views/questions/_modules/QuestionDataTable.vue';
import type { Module } from '@/service/api';
import { deleteModule, fetchModules } from '@/service/api';
import UpdateModule from '@/views/questions/_modules/UpdateModule.vue';
import AddModule from './_modules/AddModule.vue';

const message = useMessage();
const showAddModule = ref<boolean>(false);
const showUpdateModule = ref<boolean>(false);
const currentModule = ref<Module | undefined>();
const currentEditModule = ref<Module | undefined>();

function getRowProps(row: Module, index: number) {
  return {
    onClick() {
      currentModule.value = row;
    }
  };
}
const tableColumns: DataTableColumns<Module> = [
  {
    key: 'no',
    title: 'No',
    width: 50,
    render(_, index: number) {
      return `${index + 1}`;
    }
  },
  {
    key: 'label',
    title: 'label'
  },
  {
    key: 'name',
    title: '名称'
  },
  {
    key: 'bgColor',
    title: '主题色',
    width: 80,
    render(row: Module) {
      return h('input', {
        type: 'color',
        value: row.bgColor,
        disabled: true,
        'onUpdate:value': newValue => {
          row.bgColor = newValue;
        }
      });
    }
  },
  {
    key: 'displayOrder',
    title: '排序',
    width: 60
  },
  {
    key: 'op',
    title: '操作',
    width: 100,
    render(row: Module) {
      const deleteEl = h(
        NPopconfirm,
        {
          async onPositiveClick() {
            const { error } = await deleteModule(row.id);
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
              text: true,
              onClick(e) {
                e.stopPropagation();
              }
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
          onClick(e) {
            e.stopPropagation();
            currentEditModule.value = row;
            openUpdateModule();
          }
        },
        '编辑'
      );
      return h(NSpace, null, [deleteEl, editEl]);
    }
  }
];
async function fetchData() {
  const { data, error } = await fetchModules();
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
const { columns, data, loading, getData } = useTable<Module>({
  columns: () => tableColumns,
  apiFn: fetchData,
  apiParams: {
    page: 1,
    size: 1000
  }
});
async function refresh() {
  await getData();
}
function openAddModule() {
  showAddModule.value = true;
}
function openUpdateModule() {
  showUpdateModule.value = true;
}
function close() {
  showAddModule.value = false;
}
</script>

<template>
  <div class="m-2 rounded-1">
    <NLayout class="h-full" has-sider>
      <NLayoutSider
        width="40%"
        collapse-mode="transform"
        bordered
        collapsed-width="0"
        show-trigger
        :show-collapsed-content="false"
      >
        <div class="h-full flex flex-col gap-y-2 p-1">
          <NButton class="self-start" type="primary" size="small" @click="openAddModule">
            <template #icon>
              <icon-material-symbols-light-add-circle-outline />
            </template>
            添加模块
          </NButton>
          <NDataTable
            class="flex-grow"
            flex-height
            pagination
            :columns="columns"
            :data="data"
            :loading="loading"
            :row-props="getRowProps"
            remote
          ></NDataTable>
        </div>
      </NLayoutSider>
      <NLayoutContent content-class="h-full">
        <div v-if="!currentModule" class="h-full flex flex-col items-center justify-center">
          <icon-mdi-hand-tap class="text-3xl" />
          <p class="mt-1">请选择模块</p>
        </div>
        <QuestionDataTable v-else class="flex-grow" :module="currentModule"></QuestionDataTable>
      </NLayoutContent>
    </NLayout>
    <AddModule v-model:show="showAddModule" :default-order="data.length" @success="refresh" @close="close"></AddModule>
    <UpdateModule
      v-model:show="showUpdateModule"
      :module-id="currentEditModule?.id"
      @success="refresh"
      @close="close"
    ></UpdateModule>
  </div>
</template>

<style></style>
