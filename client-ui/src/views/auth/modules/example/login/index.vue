<script setup lang="ts">
import EmailLogin from './email.vue'
import PasswordLogin from './password.vue'

</script>

<template>
<div>
  <NTabs default-value="password" justify-content="center">
    <NTabPane name="code" tab="验证码登录">
      <EmailLogin></EmailLogin>
    </NTabPane>
    <NTabPane name="password" tab="密码登录">
      <PasswordLogin></PasswordLogin>
    </NTabPane>
  </NTabs>
</div>
</template>

<style scoped>

</style>
