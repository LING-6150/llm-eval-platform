<template>
  <div class="not-found-page">
    <div class="container-custom py-16">
      <div class="not-found-content">
        <h1 class="error-code">404</h1>
        <h2 class="error-title">页面未找到</h2>
        <p class="error-message">抱歉，您访问的页面不存在或已被删除。</p>
        <router-link to="/" class="btn btn-primary mt-6">返回首页</router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.not-found-page {
  @apply min-h-screen bg-gray-50 flex items-center justify-center;
}

.not-found-content {
  @apply text-center py-16;
}

.error-code {
  @apply text-8xl font-bold text-primary mb-4;
}

.error-title {
  @apply text-2xl font-bold text-gray-800 mb-4;
}

.error-message {
  @apply text-gray-600 mb-8;
}
</style> 