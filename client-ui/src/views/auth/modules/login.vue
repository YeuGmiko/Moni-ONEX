<script setup lang="ts">
import Image from '/images/wechat-code.jpg'
import type { FormRules, FormInst } from 'naive-ui'
import { useRouter, useRoute } from 'vue-router'
import {onBeforeMount, reactive, ref} from 'vue'
import { OhVueIcon } from 'oh-vue-icons'
import { useMessage } from 'naive-ui'
import {useAuthStore} from '@/store/authStore'
import {ROUTE_NAME} from '@/router/routes'
import {storeToRefs} from 'pinia'

interface Model {
  userName: string,
  password: string,
  rememberMe: boolean
}
const router = useRouter()
const route = useRoute()
const message = useMessage()
const model = reactive<Model>({
  userName: '',
  password: '',
  rememberMe: false
})


const authStore = useAuthStore()
const { login } = authStore
const submitLoading = ref<boolean>(false)
const rules: FormRules = {
  userName: [{ required: true, trigger: 'blur', message: '用户名不能为空' }],
  password: [{ required: true, trigger: 'blur', message: '密码不能为空' }]
}

const formRef = ref<FormInst>()

async function submit() {
  try {
    if (formRef.value) await formRef.value.validate()
  } catch {
    return
  }
  submitLoading.value = true
  const error = await login(model)
  submitLoading.value = false
  if (error) return
  else message.success('登录成功')
  const redirectPath = route.query['redirect']
  if (redirectPath && typeof redirectPath !== 'object') await router.push(redirectPath)
  else await router.push({
    name: ROUTE_NAME.home
  })
}
function toRegister() {
  router.push({
    name: ROUTE_NAME.authRegister
  })
}
</script>

<template>
  <div>
    <NForm ref="formRef" class="mt-2" v-model:model="model" :rules="rules" :show-label="false" size="large">
      <NFormItem path="userName">
        <NInput placeholder="请输入用户名" v-model:value="model.userName" @keydown.enter="submit">
          <template #prefix><OhVueIcon name="hi-user-circle" scale="1.1" fill="gray"></OhVueIcon></template>
        </NInput>
      </NFormItem>
      <NFormItem path="password">
        <NInput placeholder="请输入密码" type="password" v-model:value="model.password" show-password-on="click" clearable @keydown.enter="submit">
          <template #prefix><OhVueIcon name="hi-key" scale="1.1" fill="gray"></OhVueIcon></template>
        </NInput>
      </NFormItem>
      <NFormItem :show-feedback="false">
        <NButton class="w-full" type="primary" round v-model:loading="submitLoading" @click="submit">{{ submitLoading ? '登录中' : '登录' }}</NButton>
      </NFormItem>
    </NForm>
    <p class="flex justify-between">
      <NCheckbox v-model:checked="model.rememberMe">保持登录状态</NCheckbox>
      <span>没有账号？
        <NPopover placement="bottom" trigger="click">
          <template #trigger>
            <NButton text type="info" @click.prevent="toRegister">去注册</NButton>
          </template>
          <template #default>
            <NImage :src="Image" :height="120" :show-toolbar="false"></NImage>
          </template>
        </NPopover>
      </span>
    </p>
  </div>
</template>

<style scoped>

</style>
