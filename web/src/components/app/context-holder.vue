<script setup lang="ts">
import { onMounted } from 'vue'
import { useDialog, useLoadingBar, useMessage, useNotification } from 'naive-ui'

function register() {
  // window.$loadingBar = useLoadingBar();
  // window.$dialog = useDialog();
  window.$message = useMessage()
  // window.$notification = useNotification();
}

onMounted(register)
</script>

<template>
<slot></slot>
</template>

<style scoped>

</style>
