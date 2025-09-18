<script setup lang="ts">
import type { FormRules, FormInst } from 'naive-ui'
import { reactive, ref } from 'vue'
import { OhVueIcon } from 'oh-vue-icons'
import { useMessage } from 'naive-ui'
import { useRouter } from 'vue-router'
import { fetchLogin } from '@/server/api/auth'

interface Model {
  userName: string,
  password: string,
  rememberMe: boolean
}
const message = useMessage()
const router = useRouter()
const model = reactive<Model>({
  userName: '',
  password: '',
  rememberMe: false
})

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
 const { error, response } = await fetchLogin(model)
  submitLoading.value = false
  if (error) message.error(response.data.msg)
  else message.success('登录成功')
}

// async function toRegister() {
//   await router.push({
//     name: 'register'
//   })
// }
</script>

<template>
  <div>
    <NForm ref="formRef" class="mt-2" v-model:model="model" :rules="rules" :show-label="false" size="large">
      <NFormItem path="userName">
        <NInput placeholder="请输入用户名" v-model:value="model.userName">
          <template #prefix><OhVueIcon name="hi-user-circle" scale="1.1" fill="gray"></OhVueIcon></template>
        </NInput>
      </NFormItem>
      <NFormItem path="password">
        <NInput placeholder="请输入密码" type="password" v-model:value="model.password" show-password-on="click" clearable>
          <template #prefix><OhVueIcon name="hi-key" scale="1.1" fill="gray"></OhVueIcon></template>
        </NInput>
      </NFormItem>
      <NFormItem :show-feedback="false">
        <NButton class="w-full" type="primary" round v-model:loading="submitLoading" @click="submit">{{ submitLoading ? '登录中' : '登录' }}</NButton>
      </NFormItem>
    </NForm>
    <p class="flex justify-between">
      <NCheckbox v-model:checked="model.rememberMe">保持登录状态</NCheckbox>
      <span>没有账号？<NButton text type="info" @click.prevent>去问老师</NButton></span>
    </p>
  </div>
</template>

<style scoped>

</style>
