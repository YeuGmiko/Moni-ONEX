<script setup lang="ts">
import { OhVueIcon } from 'oh-vue-icons'
type IconFilp = 'vertical' | 'horizontal' | 'both'
type IconSpeed = 'slow' | 'fast'
type IconAnimation = 'wrench' | 'ring' | 'pulse' | 'spin' | 'spin-pluse' | 'flash' | 'float'
interface Props {
  name?: string
  scale?: number
  fill?: string
  flip?: IconFilp
  speed?: IconSpeed
  animation?: IconAnimation
  label?: string
}
const props = withDefaults(defineProps<Props>(), {
  scale: 1,
  fill: 'currentColor'
})
</script>

<template>
<OhVueIcon v-bind="props">
  <slot></slot>
</OhVueIcon>
</template>

<style scoped>

</style>
