<template>
  <div class="blog-card card">
    <div class="blog-cover">
      <img :src="blog.coverImg" :alt="blog.title" class="cover-img" referrerpolicy="no-referrer" />
    </div>
    <div class="blog-content">
      <h3 class="blog-title">{{ blog.title }}</h3>
      <p class="blog-summary">{{ summary }}</p>
      <div class="blog-meta">
        <div class="blog-time">{{ formattedTime }}</div>
        <ThumbButton 
          :blog-id="blog.id" 
          :count="blog.thumbCount" 
          :has-thumb="blog.hasThumb" 
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import ThumbButton from './ThumbButton.vue';
import { formatDate, truncateString } from '../utils';

const props = defineProps({
  blog: {
    type: Object,
    required: true
  }
});

// 计算摘要信息（截取内容前100个字符）
const summary = computed(() => {
  return truncateString(props.blog.content, 100);
});

// 格式化时间
const formattedTime = computed(() => {
  return formatDate(props.blog.createTime);
});
</script>

<style scoped>
.blog-card {
  @apply flex flex-col h-full transition-transform duration-300 hover:-translate-y-1;
}

.blog-cover {
  @apply w-full overflow-hidden;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f3f4f6;
}

.cover-img {
  @apply w-full h-full object-contain transition-transform duration-500 hover:scale-105;
}

.blog-content {
  @apply flex flex-col flex-grow p-4;
}

.blog-title {
  @apply text-lg font-semibold text-gray-800 mb-2 line-clamp-2;
}

.blog-summary {
  @apply text-sm text-gray-600 mb-4 flex-grow line-clamp-3;
}

.blog-meta {
  @apply flex justify-between items-center mt-2;
}

.blog-time {
  @apply text-xs text-gray-500;
}
</style> 