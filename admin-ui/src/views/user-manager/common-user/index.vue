<script setup lang="ts">
import { h, ref } from 'vue';
import type { DataTableColumns, DataTableRowKey } from 'naive-ui';
import { NButton, NPopconfirm, NSpace, NSwitch, useMessage } from 'naive-ui';
import { useTable } from '@/hooks/common/table';
import {updateUserBan, UserInfo} from '@/service/api/user';
import { deleteCommonUser, fetchCommonUserList } from '@/service/api/user';
import UploadCommonUser from '@/views/user-manager/common-user/_modules/UploadCommonUser.vue';
// import Search from './_modules/search.vue';
import Add from './_modules/add.vue';
import Edit from './_modules/editor.vue';

const message = useMessage();
const checkedKeys = ref<DataTableRowKey[]>([]);
const showUpload = ref<boolean>(false);
const showAdd = ref<boolean>(false);
const showEdit = ref<boolean>(false);
const currentUser = ref<UserInfo>({ userId: '', userName: '', name: '', roles: [], status: 0 });

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
    key: 'status',
    title: '封禁状态',
    render(row: UserInfo) {
      return h(
        NSwitch,
        {
          value: row.status === 1,
          disabled: row.status === 0,
          onUpdateValue: async (check: boolean) => {
            const { error } = await updateUserBan(row.userId, !check);
            if (error) return;
            message.success(check ? '解封成功' : '封禁成功');
            await refresh();
          }
        },
        { checked: () => '正常', unchecked: () => (row.status === 0 ? '不可用' : '封禁') }
      );
    }
  },
  {
    key: 'op',
    title: '操作',
    width: 100,
    render(row: UserInfo) {
      return h(NSpace, {}, [
        h(
          NButton,
          {
            type: 'info',
            text: true,
            onClick: () => {
              openEdit(row);
            }
          },
          '编辑'
        ),
        h(
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
        )
      ]);
    }
  }
];
const { columns, columnChecks, data, loading, getData } = useTable({
  columns: () => useTableColumns,
  apiFn: fetchData,
  immediate: true
});

function getRowKey(row: UserInfo) {
  return row.userId;
}
function handleCheck(keys: string[]) {
  checkedKeys.value = keys;
}
function openEdit(info: UserInfo) {
  showEdit.value = true;
  currentUser.value = info;
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
      total: responseData?.length ?? 0
    }
  };
}
function handleDeleteRowKeys() {
  const deleteData = data.value.filter(item => checkedKeys.value.includes(item.userId));
}
</script>

<template>
  <div class="flex flex-col gap-y-3">
    <!--    <Search v-model:value="searchParams"></Search>-->
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
        :data="data"
        :loading="loading"
        :row-key="getRowKey"
        @update-checked-row-keys="handleCheck"
      ></NDataTable>
    </NCard>
    <Add v-model:show="showAdd" @refresh="refresh"></Add>
    <Edit v-model:show="showEdit" :info="currentUser" @refresh="refresh"></Edit>
    <UploadCommonUser v-model:show="showUpload" @success="refresh"></UploadCommonUser>
  </div>
</template>

<style scoped></style>
