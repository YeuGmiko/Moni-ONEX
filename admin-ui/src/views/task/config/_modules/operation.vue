<script setup lang="ts">
import { ref } from 'vue';
import { useMessage } from 'naive-ui';
import { putTaskQuestions } from '@/service/api/mask';

const message = useMessage();
const loading = ref(false);
const emit = defineEmits<{
  (e: 'success'): void;
}>();

async function putGenTaskQuestions() {
  loading.value = true;
  const { error } = await putTaskQuestions();
  if (error) {
    loading.value = false;
    return;
  }
  message.success('生成成功');
  loading.value = false;
  emit('success');
}
</script>

<template>
  <NCard title="操作">
    <NFormItem>
      <template #label>
        <p class="text-gray">
          题目生成是定时的（每小时一次），若重新配置后需要立即生效的话，可以点击下方按钮立即生成题目
        </p>
      </template>
      <NPopconfirm @positive-click="putGenTaskQuestions">
        确定手动生成？
        <template #trigger>
          <NButton type="warning" ghost :loading="loading">手动生成</NButton>
        </template>
      </NPopconfirm>
    </NFormItem>
  </NCard>
</template>

<style scoped></style>
