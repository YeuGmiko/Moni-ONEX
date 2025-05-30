<script setup lang="ts">
import Config from './_modules/config.vue';
import Operation from './_modules/operation.vue';
import Questions from './_modules/questions.vue';
</script>

<template>
  <div class="m-2 h-full flex gap-x-2">
    <div class="h-full flex flex-col gap-y-2">
      <Config></Config>
      <Operation class="flex-grow"></Operation>
    </div>
    <Questions></Questions>
  </div>
</template>

<style scoped></style>
