<script setup lang="ts">
import { fetchUserInfo } from '@/server/api/auth'
import {reactive, onMounted} from 'vue'
import { useMessage } from 'naive-ui'
import {useAuthStore} from '@/store/authStore'
import type { UserInfo } from '@/server/api/types'
import { fetchCurrentUser } from '@/server/api/user'

const authStore = useAuthStore()
const message = useMessage()
const model = reactive<UserInfo>({
  userId: '',
  name: '',
  userName: '',
  roles: []
})
const { getLoginUser } = authStore

async function submit() {
  const { error } = await fetchCurrentUser(model)
  if (error) return
  message.success('信息修改成功')
  await getLoginUser()
}

onMounted(async() => {
  const { error, data } = await fetchUserInfo()
  if (error) {
    message.error(error.message)
    return
  }
  Object.assign(model, data)
})
</script>

<template>
  <NForm label-placement="left">
    <NFormItem label="用户昵称">
      <NInput v-model:value="model.name" placeholder="请输入用户昵称"></NInput>
    </NFormItem>
    <NFormItem>
      <NButton type="primary" @click.prevent="submit">保存</NButton>
    </NFormItem>
  </NForm>
</template>

<style scoped>

</style>
