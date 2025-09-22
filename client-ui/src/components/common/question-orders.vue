<script setup lang="ts">
import type {Module, OrderProperties} from '@/server/api/types'
import {ref, computed, toRefs, watch} from 'vue'
import {fetchQuestionOrders} from '@/server/api/questions'

interface Props {
  module: Module
  currentOrder?: number
}

const emit = defineEmits<{
  // eslint-disable-next-line no-unused-vars
  (e: 'choose', order: OrderProperties): void
}>()

const props = withDefaults(defineProps<Props>(), {
  currentOrder: () => -1
})
const { module, currentOrder } = toRefs(props)
const isLoading = ref<boolean>(false)
const pageSize = 50

const page = ref(Math.ceil(currentOrder.value / pageSize))
const pages = computed(() => Math.ceil(module.value.questionCount / pageSize))
const orders = ref<OrderProperties[]>([])

async function fetchOrders(p?: number) {
  try {
    isLoading.value = true
    const { error, data } = await fetchQuestionOrders(module.value.id, p ?? page.value, pageSize)
    if (error) return
    orders.value = data.records
  } finally {
    isLoading.value = false
  }
}

function handlerOrderClick(order: OrderProperties) {
  emit('choose', order)
}

function onPageChanged(newPage: number) {
  page.value = newPage
}

watch(currentOrder, async () => {
  const newPage = Math.ceil(currentOrder.value / pageSize)
  if (newPage !== page.value) {
    await fetchOrders(newPage)
  }
  page.value = newPage
  handlerOrderClick(orders.value.find(o => o.order === currentOrder.value))
}, { immediate: true })

/* 修得最无语的一次，小朋友千万不要学哦 */
setTimeout(() => {
  fetchOrders()
  watch(page, fetchOrders)
}, 100)
</script>

<template>
    <div class="question-orders" :style="{
      '--theme-color': module.bgColor
    }">
      <template v-if="isLoading">
        <div class="loading">
          <Icon name="ri-loader-2-line" :scale="1.5" animation="spin" speed="slow"></Icon>
          <p class="mt-1 text-[12px]">题目加载中，请稍等...</p>
        </div>
      </template>
      <template v-else>
        <div class="orders">
          <span v-for="(order, index) in orders" :key="order.id"
                class="order-item"
                :class="{
                'current': currentOrder === order.order,
                'wrong': order.accomplishStatus === 1 && currentOrder !== order.order,
                'right': order.accomplishStatus === 2 && currentOrder !== order.order,
             }"
                @click="handlerOrderClick(order)"
          >{{ order.order }}</span>
        </div>
        <NPagination class="mt-1" :page="page" :pageSize="pageSize" :page-count="pages" size="small" :page-slot="4" @update:page="onPageChanged"></NPagination>
      </template>
    </div>
</template>

<style scoped>
.question-orders {
  /* default value */
  --item-size: 35px;
  --theme-color: darkgray;
  --right-color: var(--theme-color);
  --wrong-color: color-mix(in srgb, var(--theme-color) 60%, red);

  padding: 4px;
  width: max-content;
  height: min-content;
  border: 2px solid var(--theme-color);
  border-radius: 2px;
  background-color: color-mix(in srgb, var(--theme-color) 20%, #ffffff);

  .loading {
    margin: 15px 20px;
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: var(--theme-color);
  }

  .orders {
    display: grid;
    gap: 5px;
    grid-template-columns: repeat(5, var(--item-size));
    grid-auto-rows: var(--item-size);
  }

  .order-item {
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--theme-color);
    background-color: transparent;
    border: 2px solid var(--theme-color);
    cursor: pointer;
    user-select: none;
    border-radius: 2px;

    /* 未提交 */
    &.current {
      color: var(--theme-color);
      background-color: white;
    }

    &.right {
      color: white;
      background-color: var(--right-color);
    }

    &.wrong {
      color: white;
      border-color: var(--wrong-color);
      background-color: var(--wrong-color);
    }
  }
}
</style>
