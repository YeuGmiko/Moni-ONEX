<script setup lang="ts">
import {onMounted, ref} from 'vue'
import {checkImageExists} from '@/utils/system'

const bgImageUrl = ref<string | null>(null)

function getImageUrl(order: number) {
  return `/images/login-images/login-bg-order-${order}.png`
}

async function searchMaxImageOrder() {
  async function solve(order: number) {
    if (order < 0) return -1
    const imageUrl = getImageUrl(order)
    const existed = await checkImageExists(imageUrl)
    if (!existed) return await solve(order - 1)
    else return order
  }
  /* range: 1 - 10 */
  return await solve(Math.floor(Math.random() * 10) + 1)
}
onMounted(async () => {
  const maxRange = await searchMaxImageOrder()
  const randUrl = getImageUrl(Math.floor(Math.random() * maxRange) + 1)
  if (await checkImageExists(randUrl)) bgImageUrl.value = randUrl
  else bgImageUrl.value = getImageUrl(maxRange)
})
</script>

<template>
  <div class="login-container">
    <NImage class="login-bg-img" :src="bgImageUrl ?? ''" width="100%" object-fit="cover" preview-disabled />
    <div class="login-main">
      <div class="main-container">
        <div class="flex justify-between items-center">
          <h2 class="m0">用户登录</h2>
          <p class="app-name m0">1+X<span>练习平台</span></p>
        </div>
        <RouterView v-slot="{ Component }">
          <Transition  name="component-transition" mode="out-in">
            <component :is="Component" />
          </Transition>
        </RouterView>
      </div>
    </div>
    <div class="absolute bottom-0 p-1 text-center text-gray-500 text-[0.5rem]">
      Website Powered By <a href="https://github.com/YeuGmiko" target="_blank">葉羽.Gmiko</a>
    </div>
  </div>
</template>

<style scoped>
/* RouterView Transition */
.component-transition-enter-active, .component-transition-leave-active {
  transition: opacity .3s ease-in-out;
}
.component-transition-enter-from,
.component-transition-leave-to {
  opacity: 0;
}
.login-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;

  .login-bg-img {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
    background-position: center;
  }

  .login-main {
    position: relative;
    max-width: 400px;
    width: 100%;

    .app-name {
      margin: 10px 0;
      display: flex;
      justify-content: center;
      align-items: center;
      gap: 10px;
      font-weight: bold;
      font-size: 1.2rem;
      user-select: none;
      font-family: Comic Sans MS,serif;

      &>span {
        padding: 1px 10px 2px 10px;
        display: inline-block;
        color: white;
        font-size: 0.8rem;
        text-shadow: 0 0 2px black;
        background-color: black;
        border-radius: 4px;
      }
    }

    .main-container {
      padding: 10px 20px 10px 20px;
      z-index: 9999;
      box-sizing: border-box;
      width: 100%;
      background-color: white;
      border: 1px solid rgba(0, 0, 0, 0.2);
      border-radius: 10px;
      box-shadow: 2px 2px 10px 2px rgba(0, 0, 0, 0.15);
    }
  }
}
</style>
