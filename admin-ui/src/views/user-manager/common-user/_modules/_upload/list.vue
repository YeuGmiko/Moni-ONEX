<script setup lang="ts">
import { computed, h, ref, toRefs } from 'vue';
import type { TableColumns } from 'naive-ui/es/data-table/src/interface';
import { NButton, NPopconfirm } from 'naive-ui';
import type { CreateUser } from '@/service/api/user';

const props = withDefaults(
  defineProps<{
    data?: CreateUser;
  }>(),
  {
    data: () => []
  }
);
const emit = defineEmits<{
  (e: 'submit', data: CreateUser[]): void;
  (e: 'cancel'): void;
}>();
const { data } = toRefs(props);
const deletedItem = ref<CreateUser[]>([]);
const filteredData = computed<CreateUser[]>(() => {
  return data.value.filter(
    (item: CreateUser) => deletedItem.value.findIndex(target => target.userName === item.userName) == -1
  );
});
function getRowKey(row: CreateUser) {
  return row.userName;
}
const uploadTableColumns: TableColumns<CreateUser> = [
  {
    key: 'order',
    title: 'No',
    render(_: CreateUser, index: number) {
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
    key: 'password',
    title: '初始密码'
  },
  {
    key: 'op',
    title: '操作',
    render(row: CreateUser) {
      return h(
        NPopconfirm,
        {
          onPositiveClick: () => {
            deletedItem.value.push(row);
          }
        },
        {
          default: () => h('span', '请确定后删除'),
          trigger: () =>
            h(
              NButton,
              {
                ghost: true,
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
function cancel() {
  emit('cancel');
}
function submit() {
  emit('submit', filteredData.value);
}
</script>

<template>
  <div>
    <NScrollbar class="max-h-[400px]">
      <NDataTable :columns="uploadTableColumns" :data="filteredData" :row-key="getRowKey"></NDataTable>
    </NScrollbar>
    <div class="mt-2 flex justify-end gap-x-2">
      <NButton @click="cancel">取消</NButton>
      <NButton type="primary" @click="submit">提交</NButton>
    </div>
  </div>
</template>

<style scoped></style>
