<template>
  <div class="home-page">
    <div class="container-custom py-8">
      <div class="page-header">
        <h1 class="page-title">精选博客</h1>
        <LoginBadge />
      </div>
      
      <ApiLoader :loading="blogStore.loading" :fullscreen="true" />
      
      <template v-if="!blogStore.loading && blogs.length === 0">
        <div class="empty-state">
          <p>暂无博客内容</p>
        </div>
      </template>
      
      <div v-else class="blog-grid">
        <div 
          v-for="blog in blogs" 
          :key="blog.id" 
          class="blog-card-container card"
        >
          <router-link 
            :to="`/blog/${blog.id}`"
            class="blog-content-link"
          >
            <div class="blog-cover">
              <img :src="blog.coverImg" :alt="blog.title" class="cover-img" referrerpolicy="no-referrer" />
            </div>
            <div class="blog-content">
              <h3 class="blog-title">{{ blog.title }}</h3>
              <p class="blog-summary">{{ truncateString(blog.content, 100) }}</p>
            </div>
          </router-link>
          
          <div class="blog-meta">
            <div class="blog-time">{{ formatDate(blog.createTime) }}</div>
            <ThumbButton 
              :blog-id="blog.id" 
              :count="blog.thumbCount" 
              :has-thumb="blog.hasThumb" 
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useVirtualList } from '@vueuse/core';
import ThumbButton from '../components/ThumbButton.vue';
import LoginBadge from '../components/common/LoginBadge.vue';
import ApiLoader from '../components/common/ApiLoader.vue';
import { useBlogStore } from '../stores/blog';
import { useUserStore } from '../stores/user';
import { formatDate, truncateString } from '../utils';

const blogStore = useBlogStore();
const userStore = useUserStore();

// 获取博客列表
const fetchBlogs = async () => {
  await blogStore.fetchBlogs();
};

// 使用计算属性获取博客列表，确保响应式
const blogs = computed(() => blogStore.blogs);

// 初始化
onMounted(async () => {
  // 尝试恢复登录状态
  await userStore.restoreLogin();
  // 获取博客列表
  await fetchBlogs();
});
</script>

<style scoped>
.home-page {
  @apply min-h-screen bg-gray-50;
}

.page-header {
  @apply flex justify-between items-center mb-8;
}

.page-title {
  @apply text-2xl font-bold text-gray-800;
}

.empty-state {
  @apply flex items-center justify-center py-16 text-gray-500 text-lg;
}

.blog-grid {
  @apply grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6;
}

.blog-card-container {
  @apply flex flex-col h-full transition-transform duration-300 hover:-translate-y-1;
}

.blog-content-link {
  @apply block no-underline text-inherit transition-colors;
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
  @apply text-sm text-gray-600 mb-4 line-clamp-3;
}

.blog-meta {
  @apply flex justify-between items-center p-4 pt-0 mt-auto;
}

.blog-time {
  @apply text-xs text-gray-500;
}
</style> 