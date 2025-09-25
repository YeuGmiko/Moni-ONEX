<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import {ROUTE_NAME} from '@/router/routes'

interface NavigateItemProps {
  label: string;
  routeName: string;
}

const routes: NavigateItemProps[] = [
  {
    label: '首页',
    routeName: ROUTE_NAME.home,
  },
  {
    label: '排名',
    routeName: ROUTE_NAME.rank,
  }
]

const router = useRouter()
const route = useRoute()

function handlerClick(route: NavigateItemProps) {
  router.push({ name: route.routeName })
}
</script>

<template>
<div class="container">
  <div class="route-item" v-for="item in routes" :key="item.routeName"
       :class="{ 'active': route.name === item.routeName }"
       @click="handlerClick(item)"
  >
    {{ item.label }}
  </div>
</div>
</template>

<style scoped>
.container {
  display: flex;
  gap: 10px;
  align-items: center;

  .route-item {
    box-sizing: border-box;
    padding: 3px 20px;
    background-color: transparent;
    cursor: pointer;
    border-bottom: 2px solid gray;
    font-size: 16px;


    &.active {
      border-bottom: 2px solid red;
    }
  }
}
</style>
