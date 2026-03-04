<template>
  <div v-if="loading" class="api-loader-wrapper" :class="{ 'fullscreen': fullscreen }">
    <div class="api-loader">
      <div class="spinner"></div>
      <div v-if="message" class="message">{{ message }}</div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  loading: {
    type: Boolean,
    default: false
  },
  fullscreen: {
    type: Boolean,
    default: false
  },
  message: {
    type: String,
    default: '加载中...'
  }
});
</script>

<style scoped>
.api-loader-wrapper {
  @apply flex items-center justify-center bg-white bg-opacity-80 z-50;
}

.fullscreen {
  @apply fixed inset-0;
}

.api-loader {
  @apply flex flex-col items-center p-4;
}

.spinner {
  @apply w-10 h-10 border-4 border-primary border-t-transparent rounded-full animate-spin;
}

.message {
  @apply mt-2 text-gray-600 font-medium;
}

/* 全局加载样式 */
:global(.api-loading) {
  @apply overflow-hidden;
}

:global(.api-loading::after) {
  content: '';
  @apply fixed inset-0 bg-white bg-opacity-60 z-50;
}
</style> 