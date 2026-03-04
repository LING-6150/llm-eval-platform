<template>
  <div style="padding:24px">
    <a-card title="My Evaluation Tasks">
      <a-table :dataSource="tasks" :columns="columns" :loading="loading" rowKey="id" @row-click="showDetail" :rowClassName="() => 'clickable-row'" />
    </a-card>
    <a-modal v-model:open="modalVisible" title="Task Result" width="800px" :footer="null">
      <div v-if="selectedTask">
        <a-descriptions :column="2" bordered size="small" style="margin-bottom:16px">
          <a-descriptions-item label="Model">{{ selectedTask.modelName }}</a-descriptions-item>
          <a-descriptions-item label="Status">
            <a-tag :color="statusColor(selectedTask.status)">{{ selectedTask.status }}</a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="Latency">{{ selectedTask.latency }}ms</a-descriptions-item>
          <a-descriptions-item label="Tokens">{{ selectedTask.tokenCount }}</a-descriptions-item>
        </a-descriptions>
        <a-typography-paragraph><strong>Prompt:</strong></a-typography-paragraph>
        <a-typography-paragraph>{{ selectedTask.promptText }}</a-typography-paragraph>
        <a-typography-paragraph><strong>Result:</strong></a-typography-paragraph>
        <a-typography-paragraph>{{ selectedTask.resultText || selectedTask.errorMsg || 'Processing...' }}</a-typography-paragraph>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { fetchTasks } from '@/api/services'
import type { Task } from '@/api/services'

const tasks = ref<Task[]>([])
const loading = ref(false)
const modalVisible = ref(false)
const selectedTask = ref<Task | null>(null)
let timer: any = null

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id' },
  { title: 'Model', dataIndex: 'modelName', key: 'modelName' },
  { title: 'Status', dataIndex: 'status', key: 'status',
    customRender: ({ text }: any) => ({ children: text, props: {} }) },
  { title: 'Latency(ms)', dataIndex: 'latency', key: 'latency' },
  { title: 'Tokens', dataIndex: 'tokenCount', key: 'tokenCount' },
  { title: 'Created At', dataIndex: 'createTime', key: 'createTime' },
]

function statusColor(status: string) {
  const map: Record<string, string> = {
    pending: 'default', processing: 'blue',
    completed: 'green', failed: 'red'
  }
  return map[status] || 'default'
}

async function loadTasks() {
  loading.value = true
  try {
    const res: any = await fetchTasks()
    tasks.value = res.code === 0 ? res.data : []
  } finally {
    loading.value = false
  }
}

function showDetail(record: Task) {
  selectedTask.value = record
  modalVisible.value = true
}

onMounted(() => {
  loadTasks()
  timer = setInterval(loadTasks, 5000)
})

onUnmounted(() => clearInterval(timer))
</script>

<style>
.clickable-row { cursor: pointer; }
.clickable-row:hover td { background: #f5f5f5 !important; }
</style>
