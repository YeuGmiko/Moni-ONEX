<script setup lang="ts">
import type {Module, OrderProperties} from '@/server/api/types'
import { toRefs } from 'vue'

interface Props {
  module: Module
  current?: string
}

const emit = defineEmits<{
  // eslint-disable-next-line no-unused-vars
  (e: 'choose', order: OrderProperties): void
}>()

const props = withDefaults(defineProps<Props>(), {
  current: () => ''
})
const { module, current } = toRefs(props)

function handlerOrderClick(order: OrderProperties) {
  emit('choose', order)
}
</script>

<template>
    <div class="question-orders" :style="{
      '--theme-color': module.bgColor
    }">
      <span v-for="(order, index) in module.questions" :key="order.id"
            class="order-item"
            :class="{
              'current': current === order.id,
              'wrong': order.accomplishStatus === 1 && current !== order.id,
              'right': order.accomplishStatus === 2 && current !== order.id,
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
