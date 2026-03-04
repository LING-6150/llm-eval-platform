<template>
  <div style="display:flex;justify-content:center;align-items:center;height:100vh;background:#f0f2f5">
    <a-card title="LLM Evaluation Platform" style="width:400px">
      <a-form :model="formState" @finish="handleLogin">
        <a-form-item label="User ID" name="userId" :rules="[{required:true,message:'Please enter user ID'}]">
          <a-input-number v-model:value="formState.userId" style="width:100%" placeholder="Enter your user ID" :min="1" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" block :loading="loading">
            Login
          </a-button>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { login } from '@/api/services'
import { useUserStore } from '@/stores/user'

const formState = reactive({ userId: 1 })
const loading = ref(false)
const router = useRouter()
const userStore = useUserStore()

async function handleLogin() {
  loading.value = true
  try {
    const res: any = await login(formState.userId)
    if (res.code === 0) {
      userStore.setUser(res.data.id, res.data.username)
      message.success('Login successful')
      router.push('/tasks')
    } else {
      message.error('Login failed')
    }
  } catch (e) {
    message.error('Login failed')
  } finally {
    loading.value = false
  }
}
</script>
