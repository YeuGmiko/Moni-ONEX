<script setup lang="ts">
import { reactive, toRefs, watch } from 'vue';
import { useMessage } from 'naive-ui';
import type { UpdateUser, UserInfo } from '@/service/api/user';
import { updateUserInfo } from '@/service/api/user';

interface Props {
  info: UserInfo;
}
interface Emits {
  (e: 'close', submit: boolean): void;
  (e: 'refresh'): void;
}
const props = defineProps<Props>();
const { info } = toRefs(props);
const message = useMessage();
const show = defineModel<boolean>('show', { required: true });
const emit = defineEmits<Emits>();
const model = reactive<UpdateUser>({
  name: info.value.name,
  userName: info.value.userName,
  password: ''
});

// eslint-disable-next-line @typescript-eslint/no-shadow
function close(submit?: boolean) {
  show.value = false;
  emit('close', submit || false);
  if (submit) emit('refresh');
}

async function submit() {
  const { error } = await updateUserInfo(info.value.userId, model);
  if (error) return;
  message.success('修改成功');
  close(true);
}

watch(show, value => {
  if (!value) return;
  Object.assign(model, info.value);
  model.password = '';
});
</script>

<template>
  <NModal class="max-w-[500px] w-full" :show="show">
    <NCard title="编辑用户信息" closable @close="close">
      <NForm v-model="model" label-placement="left">
        <NFormItem label="用户昵称">
          <NInput v-model:value="model.name" placeholder="请输入用户昵称"></NInput>
        </NFormItem>
        <NFormItem label="注册用户名">
          <NInput v-model:value="model.userName" placeholder="请输入注册用户名"></NInput>
        </NFormItem>
        <NFormItem label="安全密码">
          <NInput v-model:value="model.password" placeholder="请输入用户安全密码"></NInput>
        </NFormItem>
        <NFormItem :show-feedback="false">
          <NButton type="primary" @click.prevent="submit">提交</NButton>
        </NFormItem>
      </NForm>
    </NCard>
  </NModal>
</template>

<style scoped></style>
