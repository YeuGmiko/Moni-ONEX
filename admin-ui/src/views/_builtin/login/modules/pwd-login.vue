<script setup lang="ts">
import { computed, reactive } from 'vue';
import { $t } from '@/locales';
import { useRouterPush } from '@/hooks/common/router';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { useAuthStore } from '@/store/modules/auth';

defineOptions({
  name: 'PwdLogin'
});

const authStore = useAuthStore();
const { toggleLoginModule } = useRouterPush();
const { formRef, validate } = useNaiveForm();

interface FormModel {
  userName: string;
  password: string;
  rememberMe: boolean;
}

const model: FormModel = reactive({
  userName: '',
  password: '',
  rememberMe: false
});

const rules = computed<Record<keyof FormModel, App.Global.FormRule[]>>(() => {
  // inside computed to make locale reactive, if not apply i18n, you can define it without computed
  const { formRules } = useFormRules();

  return {
    userName: formRules.userName,
    password: formRules.pwd,
    rememberMe: formRules.rememberMe
  };
});

async function handleSubmit() {
  await validate();
  await authStore.login(model);
}

// type AccountKey = 'super' | 'admin' | 'user';

// interface Account {
//   key: AccountKey;
//   label: string;
//   userName: string;
//   password: string;
// }

// const accounts = computed<Account[]>(() => [
//   {
//     key: 'super',
//     label: $t('page.login.pwdLogin.superAdmin'),
//     userName: 'SUPER_ADMIN',
//     password: 'SUPER_ADMIN'
//   }
// ]);

// async function handleAccountLogin(account: Account) {
//   Object.assign(model, {
//     userName: account.userName,
//     password: account.password
//   });
//   await authStore.login(model);
// }
</script>

<template>
  <NForm ref="formRef" :model="model" :rules="rules" size="large" :show-label="false" @keyup.enter="handleSubmit">
    <NFormItem path="userName">
      <NInput v-model:value="model.userName" :placeholder="$t('page.login.common.userNamePlaceholder')" />
    </NFormItem>
    <NFormItem path="password">
      <NInput
        v-model:value="model.password"
        type="password"
        show-password-on="click"
        :placeholder="$t('page.login.common.passwordPlaceholder')"
      />
    </NFormItem>
    <NSpace vertical :size="24">
      <div class="flex-y-center justify-between">
        <NCheckbox>{{ $t('page.login.pwdLogin.rememberMe') }}</NCheckbox>
        <NButton quaternary @click="toggleLoginModule('reset-pwd')">
          {{ $t('page.login.pwdLogin.forgetPassword') }}
        </NButton>
      </div>
      <NButton type="primary" size="large" round block :loading="authStore.loginLoading" @click="handleSubmit">
        {{ $t('common.confirm') }}
      </NButton>
<!--      <div class="flex-y-center justify-between gap-12px">-->
<!--        <NButton class="flex-1" block @click="toggleLoginModule('code-login')">-->
<!--          {{ $t(loginModuleRecord['code-login']) }}-->
<!--        </NButton>-->
<!--        <NButton class="flex-1" block @click="toggleLoginModule('register')">-->
<!--          {{ $t(loginModuleRecord.register) }}-->
<!--        </NButton>-->
<!--      </div>-->
<!--      <NDivider class="text-14px text-#666 !m-0">{{ $t('page.login.pwdLogin.otherAccountLogin') }}</NDivider>-->
<!--      <div class="flex-center gap-12px">-->
<!--        <NButton v-for="item in accounts" :key="item.key" type="primary" @click="handleAccountLogin(item)">-->
<!--          {{ item.label }}-->
<!--        </NButton>-->
<!--      </div>-->
    </NSpace>
  </NForm>
</template>

<style scoped></style>
