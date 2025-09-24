<script setup lang="ts">
import {toRefs} from 'vue'
import type {RuleViewItem} from './types'

interface Props {
  value: string,
  rules: RuleViewItem[]
}
const props = withDefaults(defineProps<Props>(), {
  value: '',
  rules: () => []
})
const { value, rules } = toRefs(props)
</script>

<template>
<div>
  <ul class="rule-view">
    <li :class="[new RegExp(item.rule).test(value) ? 'success' : 'error']" v-for="item in rules" :key="item.label">{{ item.label }}</li>
  </ul>
</div>
</template>

<style scoped>
.rule-view {
  margin: 0;
  padding: 0;
  list-style: none;

  li {
    display: flex;
    align-items: center;
    gap: 10px;

    &:before {
      content: '';
      width: 8px;
      height: 8px;
      border-radius: 50%;
      background-color: currentColor;
    }

    &.success {
      color: green;
    }
    &.error {
      color: red;
    }
  }
}
</style>
