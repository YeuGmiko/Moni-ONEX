<script setup lang="ts">
import type { UploadFileInfo, UploadInst } from 'naive-ui';
import { useMessage } from 'naive-ui';
import { computed, ref } from 'vue';
import { parseUserExcel } from '@/service/api/excel';
import type { CreateUser } from '@/service/api/user';

const emit = defineEmits<{
  (e: 'success', data: CreateUser[]);
  (e: 'cancel');
}>();
const message = useMessage();
const uploadRef = ref<UploadInst | null>(null);
const fileList = ref<UploadFileInfo[]>([]);
/* 上传的文件 */
const uploadedFile = computed<File | undefined>(() => {
  const file = fileList.value[0];
  return file?.file;
});

function handleUploadFile(options: { file: UploadFileInfo; event?: ProgressEvent }) {
  const { file } = options;
  if (!file.name.endsWith('.xlsx') || !file.name.endsWith('xlsx')) {
    message.warning('仅支持xlsx、xls等格式的文件');
    return Promise.resolve(false);
  }
  return Promise.resolve(true);
}
async function submit() {
  const { data, error } = await parseUserExcel(uploadedFile.value);
  if (error) return;
  emit('success', data);
}
function cancel() {
  emit('cancel');
}
</script>

<template>
  <div>
    <NUpload ref="uploadRef" v-model:file-list="fileList" class="mb-2" :max="1" @before-upload="handleUploadFile">
      <NUploadDragger>
        <div class="flex flex-col items-center gap-y-2">
          <NIcon size="48" :depth="3">
            <icon-mdi-cloud-upload></icon-mdi-cloud-upload>
          </NIcon>
          <NText class="text-3">点击或者拖动文件到该区域来上传</NText>
        </div>
        <NP depth="3">仅支持 md、txt 文件，文件数据必须是指定的格式</NP>
      </NUploadDragger>
    </NUpload>
    <div class="flex justify-end gap-x-2">
      <NButton @click="cancel">取消</NButton>
      <NButton type="primary" :disabled="!Boolean(uploadedFile)" @click="submit">上传</NButton>
    </div>
  </div>
</template>

<style scoped></style>
