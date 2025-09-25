<script setup lang="ts">
import { fetchUserInfo } from '@/server/api/auth'
import {reactive, onMounted, ref} from 'vue'
import { useMessage } from 'naive-ui'
import type { UserInfo } from '@/server/api/types'
import UpdatePassword from './updatePassword.vue'

const message = useMessage()
const model = reactive<UserInfo>({
  userId: '',
  name: '',
  userName: '',
  roles: []
})

const showUpdatePassword = ref<boolean>(false)
function openUpdatePassword() {
  showUpdatePassword.value = true
}
function closeUpdatePassword() {
  showUpdatePassword.value = false
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
  <div>
    <NForm label-placement="left">
      <NFormItem label="注册用户名">
        {{ model.userName }}
      </NFormItem>
      <NFormItem label="安全密码">
        <NButton type="info" @click="openUpdatePassword">点击更改</NButton>
      </NFormItem>
    </NForm>
    <UpdatePassword :show="showUpdatePassword" @close="closeUpdatePassword"></UpdatePassword>
  </div>
</template>

<style scoped>

</style>
