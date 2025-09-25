<script setup lang="ts">
import {ref, h, reactive, watch} from 'vue'
import type {Pagination, RankInfo} from '@/server/api/types'
import {DataTableColumns} from 'naive-ui'
import {fetchDailyRankLIst, fetchRankList} from '@/server/api/rank'
import {FlatResponseData} from '@/server'

type RankType = 'today' | 'total'
const tabName = ref<RankType>('today')
const rankList = ref<RankInfo[]>([])
const isLoading = ref<boolean>(false)
const pagination = reactive<{ current: number, size: number }>({
  current: 1,
  size: 20
})

const columns: DataTableColumns = [
  {
    title: '排名',
    key: 'order'
  },
  {
    title: '昵称',
    key: 'nickname'
  },
  {
    title: '提交正确数',
    key: 'acCount'
  },
  {
    title: '总提交数',
    key: 'submits'
  },
  {
    title: '提交正确率',
    render(row: RankInfo) {
      return h('span', {}, `${(row.submits <= 0 ? 0 : (row.acCount / row.submits)) * 100}%`)
    }
  }
]

async function fetchData() {
  isLoading.value = true
  try {
    let response: FlatResponseData<Pagination<RankInfo>> | null = null
    if (tabName.value === 'today') {
      response = await fetchDailyRankLIst(pagination.current, pagination.size)
    } else {
      response = await fetchRankList(pagination.current, pagination.size)
    }
    const { error, data } = response
    if (error || !data) return
    rankList.value = data.records
  } finally {
    isLoading.value = false
  }
}

watch(tabName, fetchData, { immediate: true })
</script>

<template>
<div class="page-container">
  <div class="main-content">
    <NTabs v-model:value="tabName" class="max-w-[500px] w-full" type="segment">
      <NTabPane tab="今日排名" name="today"></NTabPane>
      <NTabPane tab="总排名" name="total"></NTabPane>
    </NTabs>
    <NDataTable :data="rankList" :columns="columns" :loading="isLoading" bordered bottom-bordered></NDataTable>
  </div>
</div>
</template>

<style scoped>
.page-container {
  padding: 20px;

  .main-content {
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    align-items: center;
    max-width: 1200px;
    width: 100%;
  }
}
</style>
