<template>
  <div style="padding:24px;max-width:800px;margin:0 auto">
    <a-card title="Submit Evaluation Task">
      <a-form @finish="handleSubmit" layout="vertical">
        <a-form-item label="Prompt" name="promptText" :rules="[{required:true,message:'Please enter a prompt'}]">
          <a-textarea v-model:value="promptText" :rows="6" placeholder="Enter your prompt here..." />
        </a-form-item>
        <a-form-item label="Model" name="modelName">
          <a-select v-model:value="modelName" style="width:100%">
            <a-select-option value="deepseek-chat">DeepSeek Chat</a-select-option>
            <a-select-option value="gpt-3.5-turbo">GPT-3.5 Turbo</a-select-option>
            <a-select-option value="gpt-4">GPT-4</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" :loading="loading">Submit</a-button>
        </a-form-item>
      </a-form>
      <a-alert v-if="taskId" type="success" :message="`Task submitted! ID: ${taskId}`" show-icon style="margin-top:16px" />
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import { submitEval } from '@/api/services'

const promptText = ref('')
const modelName = ref('deepseek-chat')
const loading = ref(false)
const taskId = ref<string | null>(null)

async function handleSubmit() {
  loading.value = true
  try {
    const res: any = await submitEval(promptText.value, modelName.value)
    if (res.code === 0) {
      taskId.value = res.data
      message.success('Task submitted successfully')
      promptText.value = ''
    } else {
      message.error('Submit failed')
    }
  } catch (e) {
    message.error('Submit failed')
  } finally {
    loading.value = false
  }
}
</script>
