<script setup lang="ts">
import { reactive, ref, toRefs, watch } from 'vue';
import type { FormInst, FormRules } from 'naive-ui';
import { useMessage } from 'naive-ui';
import type { PostModule } from '@/service/api/module';
import { postModule } from '@/service/api/module';

const message = useMessage();
const showModel = defineModel<boolean>('show', {
  required: true
});
const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'success'): void;
}>();
const props = withDefaults(
  defineProps<{
    defaultOrder?: number;
  }>(),
  {
    defaultOrder: 0
  }
);
const { defaultOrder } = toRefs(props);
const formRef = ref<FormInst>();
const model = reactive<PostModule>({
  label: '',
  name: '',
  bgColor: '#efefef',
  order: defaultOrder.value,
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
  const { error } = await postModule(model);
  if (error) return;
  message.success('新增成功');
  reset();
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
  reset();
  emit('close');
}
function reset() {
  Object.assign(model, {
    label: '',
    name: '',
    bgColor: '#efefef',
    order: defaultOrder.value,
    remark: ''
  });
}

watch(defaultOrder, newValue => (model.order = newValue));
</script>

<template>
  <NModal v-model:show="showModel" auto-focus :mask-closable="false" display-directive="if" @close="close">
    <NCard class="max-w-[400px] w-[80%]" title="添加模块" closable @close="close">
      <NForm
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
          <NInputNumber
            v-model:value="model.order"
            placeholder="请输入顺序，小数靠前"
            :default-value="defaultOrder"
            :min="0"
          ></NInputNumber>
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
            <NButton type="primary" @click="submit">添加</NButton>
          </div>
        </NFormItem>
      </NForm>
    </NCard>
  </NModal>
</template>

<style scoped></style>
