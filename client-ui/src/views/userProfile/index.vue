<script setup lang="ts">
import CustomInfo from './modules/customInfo.vue'
import SecureInfo from './modules/secureInfo.vue'
import { useRouter } from 'vue-router'
import {ROUTE_NAME} from '@/router/routes'

const router = useRouter()
async function routerToHome() {
  await router.push({ name: ROUTE_NAME.home })
}
</script>

<template>
  <div class="page-container">
    <span class="router-back w-[max-content]" @click="routerToHome">
      <Icon class="icon" name="ri-arrow-go-back-fill"></Icon>
      <span class="text">返回首页</span>
    </span>
    <n-tabs class="main" type="segment" animated pane-class="mt-2 rounded bg-white">
      <n-tab-pane name="chap1" tab="个性化信息">
        <CustomInfo class="m-2"></CustomInfo>
      </n-tab-pane>
      <n-tab-pane name="chap2" tab="账号安全信息">
        <SecureInfo class="m-2"></SecureInfo>
      </n-tab-pane>
    </n-tabs>
  </div>
</template>

<style scoped>
.page-container {
  padding: 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  .main {
    margin-top: 10px;
    width: 100%;
    max-width: 600px;
  }
}
.router-back {
  display: flex;
  align-items: center;
  gap: 5px;
  width: 100%;
  max-width: 600px;
  cursor: pointer;

  .icon {
    padding: 4px;
    border: 2px solid black;
    border-radius: 4px;
  }

  .text {
    font-size: 0.8rem;
    font-weight: bold;
  }
}
</style>
