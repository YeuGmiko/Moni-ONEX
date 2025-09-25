<script setup lang="ts">
import {toRefs, reactive} from 'vue'
import {updatePassword, UpdatePasswordBody} from '@/server/api/auth'
import {useMessage} from "naive-ui";

interface Props {
  show: boolean
}
interface Emits {
  // eslint-disable-next-line no-unused-vars
  (e: 'close'): void
}
const message = useMessage()
const props = defineProps<Props>()
const emits = defineEmits<Emits>()
const { show } = toRefs(props)
const model = reactive<UpdatePasswordBody & { repeatNewPassword: string }>({
  oldPassword: '',
  newPassword: '',
  repeatNewPassword: ''
})

function close() {
  emits('close')
}

async function submit() {
  if (model.newPassword !== model.repeatNewPassword) message.warning('两次输入的新密码不一致')
  const { error } = await updatePassword(model)
  if (error) return
  message.success('密码更新成功')
  close()
}
</script>

<template>
<NModal class="container" v-model:show="show">
  <NCard title="设置/更新密码" closable @close="close">
    <NForm label-placement="left">
      <NFormItem label="旧密码">
        <NInput type="password" show-password-toggle v-model:value="model.oldPassword" placeholder="请输入旧密码"></NInput>
      </NFormItem>
      <NFormItem label="新密码">
        <NInput type="password" show-password-toggle v-model:value="model.newPassword" placeholder="请输入新密码"></NInput>
      </NFormItem>
      <NFormItem label="重复新密码">
        <NInput type="password" show-password-toggle v-model:value="model.repeatNewPassword" placeholder="请重复输入新密码"></NInput>
      </NFormItem>
      <NFormItem :show-feedback="false">
        <NButton type="primary" @click.prevent="submit">提交</NButton>
      </NFormItem>
    </NForm>
  </NCard>
</NModal>
</template>

<style scoped>
.container {
  width: 100%;
  max-width: 500px;
}
</style>
