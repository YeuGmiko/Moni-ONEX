<script setup lang="ts">
import type { FormInst, FormRules } from 'naive-ui';
import { useMessage } from 'naive-ui';
import { reactive, ref } from 'vue';
import type { CreateUser } from '@/service/api/user';
import { postCommonUser } from '@/service/api/user';

const showModel = defineModel<boolean>('show', {
  required: true
});
const emit = defineEmits<{
  (e: 'refresh'): void;
  (e: 'close'): void;
}>();
const message = useMessage();
const formRef = ref<FormInst | null>(null);
const model = reactive<CreateUser>({
  name: null,
  userName: null,
  password: null
});
const rules: FormRules = {
  name: [{ required: true, message: '姓名不能为空' }],
  userName: [{ required: true, message: '用户名不能为空' }],
  password: [{ required: true, message: '初始密码不能为空' }]
};
function submit() {
  if (!formRef.value) return;
  formRef.value.validate(async errors => {
    if (errors) return;
    const { error } = await postCommonUser(model);
    if (error) return;
    message.success('添加成功');
    showModel.value = false;
    emit('refresh');
    emit('close');
  });
}
function close(): void {
  showModel.value = false;
  reset();
  emit('refresh');
  emit('close');
}
function reset(): void {
  Object.assign(model, {
    name: '',
    userName: '',
    password: ''
  });
}
</script>

<template>
  <div>
    <NModal v-model:show="showModel" :mask-closable="false" @close="close">
      <NCard class="max-w-[800px] w-4/5" title="新增" closable @close="close">
        <NForm ref="formRef" v-model:model="model" :rules="rules" label-align="left">
          <NFormItem label="姓名" path="name">
            <NInput v-model:value="model.name" placeholder="请输入用户姓名"></NInput>
          </NFormItem>
          <NFormItem label="用户名" path="userName">
            <NInput v-model:value="model.userName" placeholder="请输入注册用户名"></NInput>
          </NFormItem>
          <NFormItem label="初始密码" path="password">
            <NInput v-model:value="model.password" placeholder="请输入用户初始密码"></NInput>
          </NFormItem>
        </NForm>
        <div class="flex justify-end gap-x-2">
          <NButton @click="close">取消</NButton>
          <NButton type="primary" @click="submit">提交</NButton>
        </div>
      </NCard>
    </NModal>
  </div>
</template>

<style scoped></style>
