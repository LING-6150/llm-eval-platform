<template>
  <a-layout style="min-height:100vh">
    <AppHeader v-if="userStore.isLoggedIn" />
    <a-layout-content style="padding:0">
      <router-view />
    </a-layout-content>
  </a-layout>
</template>

<script setup lang="ts">
import AppHeader from '@/components/AppHeader.vue'
import { useUserStore } from '@/stores/user'
const userStore = useUserStore()
</script>
