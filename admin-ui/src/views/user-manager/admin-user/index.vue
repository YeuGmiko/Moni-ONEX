<script setup lang="ts">
import { h, ref } from 'vue';
import type { DataTableColumns, DataTableRowKey } from 'naive-ui';
import { NButton, NPopconfirm, useMessage } from 'naive-ui';
import { useTable } from '@/hooks/common/table';
import type { UserInfo } from '@/service/api/user';
import { deleteAdminUser, fetchAdminUserList } from '@/service/api/user';
import Search from './_modules/search.vue';
import Add from './_modules/add.vue';

const message = useMessage();
const checkedKeys = ref<DataTableRowKey[]>([]);
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
    title: '用户昵称',
    align: 'center'
  },
  {
    key: 'userName',
    title: '用户名',
    align: 'center'
  },
  {
    key: 'op',
    title: '操作',
    width: 100,
    align: 'center',
    render(row: UserInfo) {
      return h(
        NPopconfirm,
        {
          onPositiveClick: async () => {
            const { error } = await deleteAdminUser(row.userId);
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
const { columns, columnChecks, data, loading, getData, searchParams, mobilePagination } = useTable<UserInfo>({
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
function openAdd() {
  showAdd.value = true;
}
function getRowKey(row: RowData) {
  return row.userId;
}
function handleCheck(keys: string[]) {
  checkedKeys.value = keys;
}
async function fetchData() {
  // eslint-disable-next-line @typescript-eslint/no-shadow
  const { data } = await fetchAdminUserList();
  return {
    data: {
      records: data
    }
  };
}
async function refresh() {
  await getData();
}
</script>

<template>
  <div class="flex flex-col gap-y-3">
    <Search v-model:value="searchParams"></Search>
    <NCard title="用户列表" class="flex-grow">
      <template #header-extra>
        <TableHeaderOperation v-model:columns="columnChecks" :loading="loading" @add="openAdd" @refresh="getData" />
      </template>
      <NDataTable
        size="small"
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
  </div>
</template>

<style scoped></style>
