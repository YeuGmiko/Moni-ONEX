<script setup lang="ts">
import { toRefs } from 'vue'
interface Props {
  label?: string
  active?: boolean
  themeColor?: string
}
const props = withDefaults(defineProps<Props>(), {
  label: '',
  active: false,
  themeColor: '#efefef',
})
const { label, active, themeColor } = toRefs(props)
</script>

<template>
  <span class="module-tag"
    :class="{
      'active': active
    }"
    :style="{ '--bg-color': themeColor }">
    <slot>{{ label }}</slot>
  </span>
</template>

<style scoped>
.module-tag {
  /* default */
  --bg-color: v-bind(themeColor);

  padding: 5px 10px;
  box-sizing: border-box;
  color: white;
  border: 2px solid var(--bg-color);
  background-color: var(--bg-color);
  border-radius: 3px;
  font-weight: bold;
  text-wrap: nowrap;
  box-shadow: 1px 1px 2px 1px rgba(0, 0, 0, 0.05);

  &.active {
    color: var(--bg-color);
    background-color: white;
    border: 2px solid var(--bg-color);
  }
}
</style>
