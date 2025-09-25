<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/authStore'
import {ROUTE_NAME} from '@/router/routes'
import { useMessage } from 'naive-ui'


interface RouteProps {
  label: string
  icon: string
  beforeFn?: () => Promise<boolean>
  routeName: string
  routeParams?: Record<string, any>
  routeQuery?: Record<string, any>
}

const routes: RouteProps[] = [
  {
    label: '个人资料',
    icon: 'ri-user-3-line',
    routeName: ROUTE_NAME.userProfile,
  },
  {
    label: '退出登录',
    icon: 'ri-logout-box-r-line',
    beforeFn: async () => {
      await authStore.logout(true)
      message.success('退出成功')
      return true
    },
    routeName: ROUTE_NAME.authLogin,
  },
]
const router = useRouter()
const message = useMessage()
const authStore = useAuthStore()

async function handlerClick(route: RouteProps) {
  route.beforeFn && await route.beforeFn()
  await router.push({ name: route.routeName, params: route.routeParams, query: route.routeQuery })
}
</script>

<template>
  <div class="user-button__operation">
    <div v-for="route in routes" class="op-item" @click.prevent="handlerClick(route)">
      <Icon class="icon" :name="route.icon"></Icon>
      <span class="text">{{ route.label }}</span>
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
