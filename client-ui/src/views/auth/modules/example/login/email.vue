<script setup lang="ts">
import type { FormRules, FormInst, FormItemInst} from 'naive-ui'
import {OhVueIcon} from 'oh-vue-icons'
import {reactive, ref} from 'vue'
import { useMessage } from 'naive-ui'
import { useRouter } from 'vue-router'
import {fetchEmailLoginCaptcha, loginByEmailCode} from '@/server/api/auth'

interface Model {
  email: string,
  captcha: string,
  rememberMe: boolean
}

const model = reactive<Model>({
  email: '',
  captcha: '',
  rememberMe: false
})
const message = useMessage()
const router = useRouter()
const captchaSend = ref<boolean>(false)
const captchaLoading = ref<boolean>(false)
const captchaLoadingSecond = ref<number>(0)
const submitLoading = ref<boolean>(false)

const formRef = ref<FormInst>()
const emailFormRef = ref<FormItemInst>()

const rules: FormRules = {
  email: [
    {
      required: true,
      trigger: ['blur', 'input'],
      validator(rule, value) {
        if (!value || value === '') return new Error('邮箱不能为空')
        return new RegExp(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/).test(value) || new Error('请输入正确的邮箱')
      }
    }
  ],
  captcha: [{ required: true, trigger: 'blur', message: '验证码不能为空' }]
}

async function fetchCaptcha() {
  try {
    if (emailFormRef.value) await emailFormRef.value.validate()
  } catch {
    return
  }
  captchaLoading.value = true
  const { error, response } = await fetchEmailLoginCaptcha(model.email)
  captchaLoading.value = false
  if (error) {
    message.error(response.data.message)
  } else {
    captchaSend.value = true
    captchaLoadingSecond.value = 60
    const timer = setInterval(() => {
      captchaLoadingSecond.value--
      if (captchaLoadingSecond.value <= 0) clearInterval(timer)
    }, 1000)
  }
}

async function submit() {
  try {
    if (formRef.value) await formRef.value.validate()
  } catch {
    return
  }
  submitLoading.value = true
  const { error, response } = await loginByEmailCode(model)
  submitLoading.value = false
  if (error) {
    message.error(response.data.message)
  } else {
    message.success('登录成功')
  }
}

async function toRegister() {
  await router.push({
    name: 'register'
  })
}
</script>

<template>
<div>
  <NForm ref="formRef" class="mt-2" v-model:model="model" :show-label="false"  size="large" :rules="rules">
    <NFormItem path="email" ref="emailFormRef">
      <NInput placeholder="请输入邮箱号" v-model:value="model.email">
        <template #prefix><OhVueIcon name="hi-mail" scale="1.1" fill="gray"></OhVueIcon></template>
      </NInput>
    </NFormItem>
    <NFormItem path="captcha">
      <NInput :placeholder="captchaSend ? '请输入验证码' : '请先获取验证码'" :disabled="!captchaSend" v-model:value="model.captcha">
        <template #prefix><OhVueIcon name="hi-code" scale="1.1" fill="gray"></OhVueIcon></template>
      </NInput>
      <NButton class="ml-1" :disabled="captchaLoadingSecond > 0" v-model:loading="captchaLoading" @click="fetchCaptcha">
        {{ captchaLoading ? '发送中' : captchaLoadingSecond > 0 ? `${captchaLoadingSecond}秒` : '发送验证码' }}
      </NButton>
    </NFormItem>
    <NFormItem :show-feedback="false">
      <NButton class="w-full" type="primary" round @click="submit" v-model:loading="submitLoading">{{ submitLoading ? '登录中' : '登录' }}</NButton>
    </NFormItem>
  </NForm>
  <p class="flex justify-between">
    <NCheckbox v-model:checked="model.rememberMe">保持登录状态</NCheckbox>
    <span>没有账号？<NButton text type="info" @click="toRegister">去注册</NButton></span>
  </p>
</div>
</template>

<style scoped>

</style>
