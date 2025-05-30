<script setup lang="ts">
import type {OrderProperties} from '@/server/api/types'
import {toRefs} from 'vue'

interface Props {
  themeColor?: string
  orders?: OrderProperties[]
  current: OrderProperties
}

const emit = defineEmits<{
  (e: 'choose', order: OrderProperties): void
}>()

const props = withDefaults(defineProps<Props>(), {
  tagLabel: '',
  themeColor: '#a3a3a3',
  orders: () => []
})
const { themeColor, orders, current } = toRefs(props)

function handlerOrderClick(order: OrderProperties) {
  emit('choose', order)
}
</script>

<template>
    <div class="question-orders" :style="{
      '--theme-color': themeColor
    }">
      <span v-for="(order, index) in orders" :key="order.id"
            class="order-item"
            :class="{
              'current': current?.id === order.id,
              'wrong': order.accomplishStatus === 1 && current?.id !== order.id,
              'right': order.accomplishStatus === 2 && current?.id !== order.id,
           }"
            @click="handlerOrderClick(order)"
      >{{ index + 1 }}</span>
    </div>
</template>

<style scoped>
.question-orders {
  /* default value */
  --item-size: 35px;
  --theme-color: darkgray;
  --right-color: var(--theme-color);
  --wrong-color: color-mix(in srgb, var(--theme-color) 60%, red);

  margin-top: 10px;
  padding: 4px;
  display: grid;
  gap: 5px;
  grid-template-columns: repeat(5, var(--item-size));
  grid-auto-rows: var(--item-size);
  width: max-content;
  height: min-content;
  border: 2px solid var(--theme-color);
  border-radius: 2px;
  background-color: color-mix(in srgb, var(--theme-color) 20%, #ffffff);

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
