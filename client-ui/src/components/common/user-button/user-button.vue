<script setup lang="ts">
import {useAuthStore} from '@/store/authStore'
import { useRouter } from 'vue-router'
import {storeToRefs} from 'pinia'
import {ROUTE_NAME} from '@/router/routes'
import UserOperationList from '@/components/common/user-button/user-operation-list.vue'

const router = useRouter()
const authStore = useAuthStore()
const { loginUser } = storeToRefs(authStore)

function toLogin() {
  if (loginUser.value) return
  router.push({ name: ROUTE_NAME.authLogin })
}
</script>

<template>
<NPopover trigger="click" display-directive="show" placement="bottom-end" raw :disabled="!Boolean(loginUser)">
  <template #trigger>
    <button class="user-button" @click="toLogin">
      <span class="user-button__inner">
        <Icon name="la-user-circle" :scale="1.4"></Icon>
        <span class="user-name">{{ loginUser?.name || '请先登录' }}</span>
      </span>
    </button>
  </template>
  <template #default>
    <UserOperationList></UserOperationList>
  </template>
</NPopover>
</template>

<style scoped>
.user-button {
  padding: 5px 10px;
  min-width: 120px;
  max-width: 180px;
  height: 40px;
  background-color: transparent;
  border: 1px solid rgba(0, 0, 0, 0.2);
  border-radius: 4px;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  box-shadow: 0 1px 1px 1px rgba(0, 0, 0, 0.05);

  .user-button__inner {
    display: flex;
    align-items: center;
    gap: 5px;
    width: 100%;

    .user-name {
      flex: 1;
      font-size: 0.8rem;
      text-wrap: nowrap;
      text-overflow: ellipsis;
      overflow: hidden;
    }
  }

  transition: background-color 0.1s ease-in;

  &:hover {
    background-color: rgba(0, 0, 0, 0.05);
  }
  &:active {
    background-color: rgba(0, 0, 0, 0.15);
  }
}
</style>
