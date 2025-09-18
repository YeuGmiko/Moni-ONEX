<script setup lang="ts">
const model = defineModel<any>('value');
const emits = defineEmits(['reset', 'search']);

function submit() {
  emits('search', model)
}
function reset() {
  emits('reset')
}
</script>

<template>
  <NCard>
    <template #header>
      <NCollapse size="large" default-expanded-names="main">
        <NCollapseItem name="main" title="搜索" display-directive="show">
          <NForm :show-feedback="false" inline label-placement="left">
            <NFormItem label="用户昵称">
              <NInput v-model:value="model.name"></NInput>
            </NFormItem>
            <NFormItem label="用户名">
              <NInput v-model:value="model.userName"></NInput>
            </NFormItem>
            <NFormItem>
              <NButton type="primary" @click="submit">搜索</NButton>
            </NFormItem>
            <NFormItem>
              <NButton type="default" @click="reset">重置</NButton>
            </NFormItem>
          </NForm>
        </NCollapseItem>
      </NCollapse>
    </template>
  </NCard>
</template>

<style scoped>

</style>
