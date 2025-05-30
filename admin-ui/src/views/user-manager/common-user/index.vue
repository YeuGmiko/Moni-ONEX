<script setup lang="ts">
import { h, ref } from 'vue';
import type { DataTableColumns, DataTableRowKey } from 'naive-ui';
import { NButton, NPopconfirm, useMessage } from 'naive-ui';
import { useTable } from '@/hooks/common/table';
import type { UserInfo } from '@/service/api/user';
import { deleteCommonUser, fetchCommonUserList } from '@/service/api/user';
import Upload from '@/views/user-manager/common-user/_modules/_upload/index.vue';
import Search from './_modules/search.vue';
import Add from './_modules/add.vue';
import UploadCommonUser from '@/views/user-manager/common-user/_modules/UploadCommonUser.vue';

const message = useMessage();
const checkedKeys = ref<DataTableRowKey[]>([]);
const showUpload = ref<boolean>(false);
const showAdd = ref<boolean>(false);

const useTableColumns: DataTableColumns<UserInfo> = [
  {
    type: 'selection'
  },
  {
    key: 'order',
    title: 'No',
    render(_: UserInfo, index: number) {
      return h('div', {}, `${index + 1}`);
    }
  },
  {
    key: 'name',
    title: '用户昵称'
  },
  {
    key: 'userName',
    title: '用户名'
  },
  {
    key: 'op',
    title: '操作',
    width: 100,
    render(row: UserInfo) {
      return h(
        NPopconfirm,
        {
          onPositiveClick: async () => {
            const { error } = await deleteCommonUser(row.userId);
            if (error) return;
            message.success('删除成功');
            await refresh();
          }
        },
        {
          default: () => h('span', '请确定后删除'),
          trigger: () =>
            h(
              NButton,
              {
                text: true,
                type: 'error',
                bordered: true
              },
              { default: () => '删除' }
            )
        }
      );
    }
  }
];
const { columns, columnChecks, data, loading, getData, searchParams, mobilePagination } = useTable({
  columns: () => useTableColumns,
  apiFn: fetchData,
  immediate: true,
  apiParams: {
    page: 1,
    size: 10,
    userId: null,
    userName: null,
    name: null
  }
});

function getRowKey(row: UserInfo) {
  return row.userId;
}
function handleCheck(keys: string[]) {
  checkedKeys.value = keys;
}
function openUpload() {
  showUpload.value = true;
}
function openAdd() {
  showAdd.value = true;
}
async function refresh() {
  await getData();
}
async function fetchData() {
  const { data: responseData } = await fetchCommonUserList();
  return {
    data: {
      records: responseData,
      page: 1,
      size: 1000,
      total: 1000
    }
  };
}
function handleDeleteRowKeys() {
  const deleteData = data.value.filter(item => checkedKeys.value.includes(item.userId));
}
</script>

<template>
  <div class="flex flex-col gap-y-3">
    <Search v-model:value="searchParams"></Search>
    <NCard title="用户列表" class="flex-grow">
      <template #header-extra>
        <div class="flex items-start gap-x-2">
          <NButton type="primary" ghost size="small" @click="openUpload">
            <template #icon>
              <icon-mdi-upload-circle-outline />
            </template>
            批量导入
          </NButton>
          <TableHeaderOperation
            v-model:columns="columnChecks"
            :loading="loading"
            @refresh="getData"
            @add="openAdd"
            @delete="handleDeleteRowKeys"
          />
        </div>
      </template>
      <NDataTable
        size="small"
        flex-height
        class="h-full"
        :columns="columns"
        :pagination="mobilePagination"
        :data="data"
        :loading="loading"
        :row-key="getRowKey"
        @update-checked-row-keys="handleCheck"
      ></NDataTable>
    </NCard>
    <Add v-model:show="showAdd" @refresh="refresh"></Add>
    <UploadCommonUser v-model:show="showUpload" @success="refresh"></UploadCommonUser>
  </div>
</template>

<style scoped></style>
