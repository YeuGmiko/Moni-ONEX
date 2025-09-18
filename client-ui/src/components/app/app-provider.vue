<script setup lang="ts">
import ContextHolder from '@/components/app/context-holder.vue'
</script>

<template>
<NMessageProvider>
  <ContextHolder>
    <slot></slot>
  </ContextHolder>
</NMessageProvider>
</template>

<style scoped>

</style>
