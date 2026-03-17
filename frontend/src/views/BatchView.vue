<template>
  <div style="padding:24px;max-width:900px;margin:0 auto">
    <a-card title="Batch Evaluation (N Prompts × M Models)">
      <a-form layout="vertical">

        <!-- Prompts输入 -->
        <a-form-item label="Prompts (one per line)">
          <a-textarea
            v-model:value="promptsText"
            :rows="6"
            placeholder="What is Redis?&#10;What is Kafka?&#10;What is Docker?"
          />
          <div style="color:#888;font-size:12px;margin-top:4px">
            {{ promptList.length }} prompt(s) entered
          </div>
        </a-form-item>

        <!-- 模型选择 -->
        <a-form-item label="Models">
          <a-checkbox-group v-model:value="selectedModels">
            <a-checkbox value="deepseek-chat">DeepSeek Chat</a-checkbox>
            <a-checkbox value="gpt-3.5-turbo">GPT-3.5 Turbo</a-checkbox>
            <a-checkbox value="gpt-4">GPT-4</a-checkbox>
          </a-checkbox-group>
          <div style="color:#888;font-size:12px;margin-top:4px">
            Total tasks: {{ promptList.length }} × {{ selectedModels.length }} = {{ promptList.length * selectedModels.length }}
          </div>
        </a-form-item>

        <a-form-item>
          <a-button
            type="primary"
            @click="handleBatchSubmit"
            :loading="loading"
            :disabled="promptList.length === 0 || selectedModels.length === 0"
          >
            Submit Batch
          </a-button>
        </a-form-item>
      </a-form>

      <!-- 提交结果 -->
      <div v-if="taskIds.length > 0" style="margin-top:16px">
        <a-alert
          type="success"
          :message="`${taskIds.length} tasks submitted successfully!`"
          show-icon
          style="margin-bottom:12px"
        />
        <a-table :dataSource="taskResults" :columns="columns" :loading="polling" row-key="id" size="small">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'status'">
              <a-tag :color="statusColor(record.status)">{{ record.status }}</a-tag>
            </template>
            <template v-if="column.key === 'latency'">
              {{ record.latency ? `${record.latency}ms` : '-' }}
            </template>
            <template v-if="column.key === 'tokens'">
              {{ record.tokenCount || '-' }}
            </template>
          </template>
        </a-table>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onUnmounted } from 'vue'
import { message } from 'ant-design-vue'
import { submitBatch, fetchTaskDetail } from '@/api/services'

const promptsText = ref('')
const selectedModels = ref<string[]>(['deepseek-chat', 'gpt-3.5-turbo'])
const loading = ref(false)
const polling = ref(false)
const taskIds = ref<string[]>([])
const taskResults = ref<any[]>([])
let pollTimer: any = null

const promptList = computed(() =>
  promptsText.value.split('\n').map(s => s.trim()).filter(s => s.length > 0)
)

const columns = [
  { title: 'Task ID', dataIndex: 'id', key: 'id', width: 80 },
  { title: 'Prompt', dataIndex: 'promptText', key: 'prompt', ellipsis: true },
  { title: 'Model', dataIndex: 'modelName', key: 'model', width: 140 },
  { title: 'Status', key: 'status', width: 110 },
  { title: 'Latency', key: 'latency', width: 100 },
  { title: 'Tokens', key: 'tokens', width: 80 },
]

function statusColor(status: string) {
  const map: any = { pending: 'default', processing: 'blue', completed: 'green', failed: 'red' }
  return map[status] || 'default'
}

async function handleBatchSubmit() {
  loading.value = true
  try {
    const res: any = await submitBatch(promptList.value, selectedModels.value)
    if (res.code === 0) {
      taskIds.value = res.data
      taskResults.value = taskIds.value.map(id => ({ id, status: 'pending', promptText: '', modelName: '', latency: 0, tokenCount: 0 }))
      message.success(`${taskIds.value.length} tasks submitted!`)
      startPolling()
    } else {
      message.error('Batch submit failed')
    }
  } catch (e) {
    message.error('Batch submit failed')
  } finally {
    loading.value = false
  }
}

function startPolling() {
  polling.value = true
  pollTimer = setInterval(async () => {
    const updates = await Promise.all(
      taskIds.value.map(id => fetchTaskDetail(id).catch(() => null))
    )
    taskResults.value = updates
      .filter(Boolean)
      .map((res: any) => res.data || res)

    const allDone = taskResults.value.every(
      t => t.status === 'completed' || t.status === 'failed'
    )
    if (allDone) {
      clearInterval(pollTimer)
      polling.value = false
      message.success('All tasks completed!')
    }
  }, 3000)
}

onUnmounted(() => {
  if (pollTimer) clearInterval(pollTimer)
})
</script>
