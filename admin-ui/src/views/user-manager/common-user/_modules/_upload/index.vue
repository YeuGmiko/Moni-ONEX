<script setup lang="ts">
import { ref } from 'vue';
import { useMessage } from 'naive-ui';
import type { CreateUser } from '@/service/api/user';
import { postCommonUserList } from '@/service/api/user';
import Upload from './upload.vue';
import List from './list.vue';

const showModel = defineModel<boolean>('show', {
  required: true
});
const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'refresh'): void;
}>();
const message = useMessage();
const tempData = ref<CreateUser[]>([]);
const disableUpload = ref<boolean>(true);
const activeName = ref<string>('upload');

function handlerUploadSuccess(data: CreateUser[]) {
  tempData.value = data;
  disableUpload.value = false;
  activeName.value = 'list';
}

async function handlerSubmit(data: CreateUser[]) {
  const { error } = await postCommonUserList(data);
  if (error) return;
  message.success('添加成功');
  showModel.value = false;
  emit('close');
}

function close(): void {
  showModel.value = false;
  emit('close');
}
</script>

<template>
  <NModal v-model:show="showModel" :mask-closable="false" @close="close">
    <NCard class="max-w-[800px] w-4/5" title="批量导入" @close="close">
      <NTabs v-model:value="activeName" animated>
        <NTabPane name="upload" tab="上传文件" display-directive="show">
          <Upload @success="handlerUploadSuccess" @cancel="close"></Upload>
        </NTabPane>
        <NTabPane name="list" tab="提交数据" :disabled="disableUpload">
          <List :data="tempData" @submit="handlerSubmit" @cancel="close"></List>
        </NTabPane>
      </NTabs>
    </NCard>
  </NModal>
</template>

<style scoped></style>
