<template>
  <a-layout-header style="background:#001529;display:flex;align-items:center;justify-content:space-between;padding:0 24px">
    <div style="color:white;font-size:18px;font-weight:bold">LLM Eval Platform</div>
    <a-menu theme="dark" mode="horizontal" :selectedKeys="[route.path]" style="flex:1;margin:0 24px">
      <a-menu-item key="/submit"><router-link to="/submit">Submit</router-link></a-menu-item>
      <a-menu-item key="/tasks"><router-link to="/tasks">Tasks</router-link></a-menu-item>
      <a-menu-item key="/stats"><router-link to="/stats">Stats</router-link></a-menu-item>
    </a-menu>
    <a-button type="text" style="color:white" @click="handleLogout">Logout</a-button>
  </a-layout-header>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>
