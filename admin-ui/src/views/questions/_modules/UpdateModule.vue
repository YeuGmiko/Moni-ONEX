<script setup lang="ts">
import { reactive, ref, toRefs, watch } from 'vue';
import type { FormInst, FormRules } from 'naive-ui';
import { useMessage } from 'naive-ui';
import type { PostModule } from '@/service/api/module';
import { fetchModule, updateModule } from '@/service/api/module';

const message = useMessage();
const showModel = defineModel<boolean>('show', {
  required: true
});
const props = withDefaults(
  defineProps<{
    moduleId?: string;
  }>(),
  {
    moduleId: ''
  }
);
const { moduleId } = toRefs(props);
const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'success'): void;
}>();
const formRef = ref<FormInst>();
const loading = ref<boolean>(false);
const submitLoading = ref(false);
const model = reactive<PostModule>({
  label: '',
  name: '',
  bgColor: '#efefef',
  order: 0,
  open: false,
  remark: ''
});
const rules: FormRules = {
  label: [{ required: true, message: 'label不能为空' }],
  name: [{ required: true, message: '名称不能为空' }],
  bgColor: [{ required: true, message: '颜色不能为空' }],
  order: [{ required: true, message: '顺序不能为空' }]
};

async function submit() {
  if (!(await validateForm())) return;
  if (!moduleId.value) {
    message.warning(`非法ID：${moduleId.value}`);
    return;
  }
  submitLoading.value = true;
  const { error } = await updateModule(moduleId.value, model);
  if (error) {
    submitLoading.value = false;
    return;
  }
  submitLoading.value = false;
  message.success('更新成功');
  emit('success');
  close();
}
async function validateForm() {
  if (!formRef.value) return false;
  try {
    await formRef.value.validate();
    return true;
  } catch {
    return false;
  }
}
function close() {
  showModel.value = false;
  emit('close');
}

watch(showModel, async newValue => {
  if (!newValue || !moduleId.value) return;
  loading.value = true;
  const { data, error } = await fetchModule(moduleId.value);
  if (error) {
    loading.value = false;
    return;
  }
  loading.value = false;
  Object.assign(model, data);
});
</script>

<template>
  <NModal v-model:show="showModel" auto-focus display-directive="if" @close="close">
    <NCard class="max-w-[350px] w-[80%]" title="编辑模块" closable @close="close">
      <div v-if="loading" class="h-[500px] flex items-center justify-center">
        <icon-line-md-loading-twotone-loop class="text-3xl" />
        <p>加载中</p>
      </div>
      <NForm
        v-else
        ref="formRef"
        v-model:model="model"
        :rules="rules"
        label-placement="left"
        label-align="right"
        label-width="auto"
      >
        <NFormItem label="label" path="label">
          <NInput v-model:value="model.label" placeholder="展示在前台的内容"></NInput>
        </NFormItem>
        <NFormItem label="名称" path="name">
          <NInput v-model:value="model.name" placeholder="用于辨别身份的名称"></NInput>
        </NFormItem>
        <NFormItem label="主题色" path="bgColor">
          <NColorPicker v-model:value="model.bgColor" :modes="['hex']" :show-alpha="false"></NColorPicker>
        </NFormItem>
        <NFormItem label="顺序" path="order">
          <NInputNumber v-model:value="model.order" placeholder="请输入顺序，小数靠前" :min="0"></NInputNumber>
        </NFormItem>
        <NFormItem label="公开状态" path="open">
          <NSwitch v-model:value="model.open" />
        </NFormItem>
        <NFormItem label="备注" path="remark">
          <NInput v-model:value="model.remark" type="textarea" placeholder="备注内容"></NInput>
        </NFormItem>
        <NFormItem :show-feedback="false">
          <div class="w-full flex justify-end gap-x-2">
            <NButton @click="close">取消</NButton>
            <NButton type="primary" :loading="submitLoading" @click="submit">提交</NButton>
          </div>
        </NFormItem>
      </NForm>
    </NCard>
  </NModal>
</template>

<style scoped></style>
