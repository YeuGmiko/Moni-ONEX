<script setup lang="ts">
import type {FormItemRuleValidator} from 'naive-ui/es/form/src/interface'
import type { FormRules } from 'naive-ui'
import type {FormInst, FormItemInst} from 'naive-ui/lib'
import type {RuleViewItem} from '@/components/RuleView/types'
import {reactive, ref} from 'vue'
import {OhVueIcon} from 'oh-vue-icons'
import {useMessage} from 'naive-ui'
import {useRouter} from 'vue-router'
import RuleView from '@/components/RuleView/RuleView.vue'
import {fetchEmailRegisterCaptcha, registerByEmailCode} from '@/server/api/auth'

interface Model {
  userName: string
  email: string
  password: string
  rePassword: string
  captcha: string
}

const model = reactive<Model>({
  userName: '',
  email: '',
  password: '',
  rePassword: '',
  captcha: ''
})
type CustomRules = Array<RuleViewItem & { error: string }>
const userNameRules: CustomRules = [
  { label: '由数字和字母组成，且不包含除 . _ 以外的其他特殊符号', rule: new RegExp(/^[a-zA-Z0-9_.]*$/g), error: '不能包含规定外的特殊字符' },
  { label: '长度为4-12之间', rule: new RegExp(/^.{4,12}$/), error: '长度必须为4-12之间' },
  { label: '至少包含1位字母', rule: new RegExp(/[a-zA-Z]+/g), error: '必须包含至少1位字母' }
]
const passwordRules: CustomRules = [
  { label: '长度位6-20之间', rule: new RegExp(/^.{6,20}$/), error: '长度必须为6-20之间' },
  { label: '至少包含1位字母', rule: new RegExp(/[a-zA-Z]+/g), error: '必须包含至少1位字母' },
  { label: '至少包含1位数字', rule: new RegExp(/\d+/g), error: '必须包含至少1位数字' }
]
function getRuleValidator(rules: CustomRules): FormItemRuleValidator {
  return function (rule, value) {
    const errorRule = rules.find(item => !new RegExp(item.rule).test(value))
    return errorRule ? new Error(errorRule.error) : true
  }
}
const rules: FormRules = {
  userName: [
    {
      required: true,
      trigger: ['input', 'blur'],
      validator: getRuleValidator(userNameRules)
    }
  ],
  email: [
    {
      required: true,
      trigger: ['blur', 'input'],
      validator(rule, value) {
        if (!value || value === '') return new Error('邮箱不能为空')
        return new RegExp(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/).test(value) ? true : new Error('请输入正确的邮箱')
      }
    }
  ],
  captcha: [{ required: true, trigger: 'blur', message: '验证码不能为空' }],
  password: [
    {
      required: true,
      trigger: ['input', 'blur'],
      validator: getRuleValidator(passwordRules)
    }
  ],
  rePassword: [
    {
      required: true,
      trigger: ['blur', 'input'],
      validator(rule, value) {
        if (!value || value === '') return new Error('请重复您的密码')
        return value === model.password ? true : new Error('两次输入的密码不一样')
      }
    }
  ]
}

const message = useMessage()
const router = useRouter()
const captchaSend = ref<boolean>(false)
const captchaLoading = ref<boolean>(false)
const captchaLoadingSecond = ref<number>(0)
const submitLoading = ref<boolean>(false)

const formRef = ref<FormInst>()
const emailFormRef = ref<FormItemInst>()

async function fetchCaptcha() {
  try {
    if (emailFormRef.value) await emailFormRef.value.validate()
  } catch {
    return
  }
  captchaLoading.value = true
  const { error, response } = await fetchEmailRegisterCaptcha(model.email)
  captchaLoading.value = false
  if (error) {
    message.error(`验证码发送失败：${response.data.message}`)
    return
  }
  captchaSend.value = true
  captchaLoadingSecond.value = 60
  const timer = setInterval(() => {
    captchaLoadingSecond.value--
    if (captchaLoadingSecond.value <= 0) clearInterval(timer)
  }, 1000)
}

async function submit() {
  try {
    if (formRef.value) await formRef.value.validate()
  } catch {
    return
  }
  submitLoading.value = true
  const { error, response } = await registerByEmailCode(model)
  submitLoading.value = false
  if (error) message.error(response.data.message)
  else {
    message.success('注册成功')
    await router.push({ name: 'login' })
  }
}

function toLogin() {
  router.push({
    name: 'login'
  })
}
</script>

<template>
<div>
  <NForm ref="formRef" :show-label="false" :rules="rules" v-model:model="model">
    <NFormItem path="userName">
      <NPopover placement="top-start" trigger="focus" display-directive="show">
        <template #trigger>
          <NInput placeholder="请输入用户名" v-model:value="model.userName">
            <template #prefix><OhVueIcon name="hi-user-circle" scale="1.1" fill="gray"></OhVueIcon></template>
          </NInput>
        </template>
        <template #default>
          <RuleView :rules="userNameRules" :value="model.userName"></RuleView>
        </template>
      </NPopover>
    </NFormItem>
    <NFormItem path="email" ref="emailFormRef">
      <NInput placeholder="请输入邮箱号" v-model:value="model.email">
        <template #prefix><OhVueIcon name="hi-mail" scale="1.1" fill="gray"></OhVueIcon></template>
      </NInput>
    </NFormItem>
    <NFormItem path="captcha">
      <NInput :placeholder="captchaSend ? '请输入验证码' : '请获取验证码'" :disabled="!captchaSend" v-model:value="model.captcha">
        <template #prefix><OhVueIcon name="hi-code" scale="1.1" fill="gray"></OhVueIcon></template>
      </NInput>
      <NButton class="ml-1" :disabled="captchaLoadingSecond > 0" v-model:loading="captchaLoading" @click="fetchCaptcha">
        {{ captchaLoading ? '发送中' : captchaLoadingSecond > 0 ? `${captchaLoadingSecond}秒` : '发送验证码' }}
      </NButton>
    </NFormItem>
    <NFormItem path="password">
      <NPopover placement="top-start" trigger="focus" display-directive="show">
        <template #trigger>
          <NInput placeholder="请输入密码" type="password" v-model:value="model.password" show-password-on="click" clearable>
            <template #prefix><OhVueIcon name="hi-key" scale="1.1" fill="gray"></OhVueIcon></template>
          </NInput>
        </template>
        <template #default>
          <RuleView :rules="passwordRules" :value="model.password"></RuleView>
        </template>
      </NPopover>
    </NFormItem>
    <NFormItem path="rePassword">
      <NInput placeholder="请重复密码" type="password" v-model:value="model.rePassword" show-password-on="click" clearable>
        <template #prefix><OhVueIcon name="hi-key" scale="1.1" fill="gray"></OhVueIcon></template>
      </NInput>
    </NFormItem>
    <NFormItem :show-feedback="false">
      <NButton class="w-full" type="primary" round @click="submit" v-model:loading="submitLoading">{{ submitLoading ? '注册中' : '注册' }}</NButton>
    </NFormItem>
  </NForm>
  <p class="flex justify-end">
    <span class="text-gray-500">已有账号？</span><NButton text type="info" size="small" @click="toLogin">去登录</NButton>
  </p>
</div>
</template>

<style scoped></style>
