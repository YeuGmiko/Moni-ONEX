<script setup lang="ts">
import { computed, h, onMounted, reactive, ref } from 'vue';
import type { FormRules, SelectOption, SelectRenderTag } from 'naive-ui';
import { NTag, useMessage } from 'naive-ui';
import type { TaskConfig } from '@/service/api/mask';
import { fetchTaskConfig, updateTaskConfig } from '@/service/api/mask';
import type { Module } from '@/service/api/question';
import { fetchModules } from '@/service/api/question';

const model = reactive<TaskConfig>({
  genCount: 0,
  modules: []
});
const message = useMessage();
const loading = ref(false);
const totalModules = ref<Module[]>([]);
const rules: FormRules = {
  genCount: [{ required: true, message: '数量不能为空' }]
};
const formModuleOptions = computed<SelectOption[]>(() => {
  return totalModules.value.map(module => ({
    label: module.name,
    value: module.id
  }));
});
async function fetchConfig() {
  const { data, error } = await fetchTaskConfig();
  if (error) return;
  Object.assign(model, data);
}
async function fetchTotalModules() {
  const { data, error } = await fetchModules();
  if (error) return;
  totalModules.value = data;
}

async function fetchData() {
  loading.value = true;
  await fetchConfig();
  await fetchTotalModules();
  loading.value = false;
}

const selectRenderTag: SelectRenderTag = ({ option, handleClose }) => {
  const module = totalModules.value.find(item => item.id === option.value);
  if (!module) return h(NTag, {}, option.label);
  return h(
    NTag,
    {
      closable: true,
      color: {
        color: module.bgColor,
        textColor: 'white'
      },
      onMousedown: (e: FocusEvent) => {
        e.preventDefault();
      },
      onClose: (e: MouseEvent) => {
        e.stopPropagation();
        handleClose();
      }
    },
    module.name
  );
};

async function submit() {
  const { error } = await updateTaskConfig(model);
  if (error) return;
  message.success('更新成功');
}

onMounted(fetchData);
</script>

<template>
  <NCard title="参数配置" size="small">
    <NForm v-model:model="model" :rules="rules">
      <NFormItem label="每日题目生成数">
        <NInputNumber
          v-model:value="model.genCount"
          class="w-auto"
          :min="0"
          :loading="loading"
          placeholder="请输入生成数"
        ></NInputNumber>
      </NFormItem>
      <NFormItem label="需要生成的模块">
        <NSelect
          v-model:value="model.modules"
          class="w-full"
          :options="formModuleOptions"
          clearable
          multiple
          :loading="loading"
          :render-tag="selectRenderTag"
          remote
        ></NSelect>
      </NFormItem>
      <NFormItem :show-label="false" :show-feedback="false">
        <NButton type="primary" @click.prevent="submit">提交</NButton>
      </NFormItem>
    </NForm>
  </NCard>
</template>

<style scoped></style>
