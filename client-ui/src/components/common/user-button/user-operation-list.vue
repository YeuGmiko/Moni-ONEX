<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/authStore'
import {ROUTE_NAME} from '@/router/routes'
import { useMessage } from 'naive-ui'

const router = useRouter()
const message = useMessage()
const authStore = useAuthStore()

function toLogin() {
  authStore.logout(true)
  router.push({ name: ROUTE_NAME.authLogin })
  message.success('退出成功')
}
</script>

<template>
  <div class="user-button__operation">
    <div class="op-item logout" @click.prevent="toLogin">
      <Icon class="icon" name="ri-logout-box-r-line"></Icon>
      <span class="text">退出登录</span>
    </div>
  </div>
</template>

<style scoped>

.user-button__operation {
  padding: 4px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  background-color: white;

  .op-item {
    padding: 6px;
    display: flex;
    align-items: center;
    gap: 10px;
    border-radius: 4px;
    transition: background-color 0.1s ease-in;
    cursor: pointer;
    text-wrap: nowrap;
    font-size: 0.8rem;

    &:hover {
      background-color: rgba(0, 0, 0, 0.08);
    }
    &:active {
      background-color: rgba(0, 0, 0, 0.15);
    }
  }
}
</style>
