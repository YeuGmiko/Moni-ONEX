<script setup lang="ts">
import LayoutHeader from './header.vue'
</script>

<template>
  <div class="layout-container">
    <LayoutHeader class="sticky top-0 left-0 w-full"></LayoutHeader>
    <NScrollbar class="flex-1" content-class="h-full">
      <div class="layout-content">
          <RouterView v-slot="{ Component }">
            <Transition  name="layout-page-transition" mode="out-in">
              <component :is="Component" />
            </Transition>
          </RouterView>
      </div>
    </NScrollbar>
    <div class="absolute self-center bottom-0 p-1 text-center text-gray-500 text-[0.5rem]">
      Website Powered By <a href="https://github.com/YeuGmiko" target="_blank">葉羽.Gmiko</a>
    </div>
  </div>
</template>

<style scoped>
/* RouterView Transition */
.layout-page-transition-enter-active, .layout-page-transition-leave-active {
  transition: opacity .15s ease-in-out;
}
.layout-page-transition-enter-from,
.layout-page-transition-leave-to {
  opacity: 0;
}
.layout-container {
  position: relative;
  display: flex;
  height: 100dvh;
  flex-direction: column;
  overflow: hidden;
  background-color: #f2f2f2;

  .layout-content {
    display: flex;
    flex-direction: column;
  }
}
</style>
