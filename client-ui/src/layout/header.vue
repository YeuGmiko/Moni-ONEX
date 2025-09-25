<script setup lang="ts">
import UserButton from '@/components/common/UserMenuButton/user-button.vue'
</script>

<template>
  <div class="layout-header">
    <div class="layout-header__content">
      <div class="app-name">
        <Icon class="app-name__icon" name="ri-moon-clear-line" :scale="1.5"></Icon>
        <span>Moni墨莉</span>
      </div>
      <div class="flex gap-x-2 items-center">
        <UserButton></UserButton>
      </div>
    </div>
  </div>
</template>

<style scoped>
.layout-header {
  padding: 2px 10px;
  box-sizing: border-box;
  box-shadow: 0 0 5px 2px rgba(0, 0, 0, 0.05);
  background-color: white;

  .layout-header__content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 50px;

    .app-name {
      display: flex;
      align-items: center;
      column-gap: 5px;
      font-family: Comic Sans MS,serif;
      font-size: 1.2rem;
      line-height: 1;
    }

  }
}
</style>
