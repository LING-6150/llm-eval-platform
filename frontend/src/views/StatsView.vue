<template>
  <div style="padding:24px">
    <a-row :gutter="16" style="margin-bottom:24px">
      <a-col :span="12">
        <a-card>
          <a-statistic title="Total Tasks" :value="stats?.totalTasks || 0" />
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card>
          <a-statistic title="Total Tokens" :value="stats?.totalTokens || 0" />
        </a-card>
      </a-col>
    </a-row>
    <a-card title="Model Comparison">
      <a-table :dataSource="stats?.modelStats || []" :columns="columns" rowKey="modelName" :loading="loading" />
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { fetchStats } from '@/api/services'
import type { Stats } from '@/api/services'

const stats = ref<Stats | null>(null)
const loading = ref(false)

const columns = [
  { title: 'Model', dataIndex: 'modelName', key: 'modelName' },
  { title: 'Task Count', dataIndex: 'taskCount', key: 'taskCount' },
  { title: 'Avg Latency(ms)', dataIndex: 'avgLatencyMs', key: 'avgLatencyMs' },
  { title: 'Total Tokens', dataIndex: 'totalTokens', key: 'totalTokens' },
  { title: 'Est. Cost(USD)', dataIndex: 'estimatedCostUSD', key: 'estimatedCostUSD' },
]

onMounted(async () => {
  loading.value = true
  try {
    const res: any = await fetchStats()
    if (res.code === 0) stats.value = res.data
  } finally {
    loading.value = false
  }
})
</script>
