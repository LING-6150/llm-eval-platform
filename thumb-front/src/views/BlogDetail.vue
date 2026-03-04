<template>
  <div class="blog-detail-page">
    <div class="container-custom py-8">
      <div class="page-header">
        <button @click="goBack" class="back-button">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="back-icon">
            <path stroke-linecap="round" stroke-linejoin="round" d="M10.5 19.5L3 12m0 0l7.5-7.5M3 12h18" />
          </svg>
          返回
        </button>
        <LoginBadge />
      </div>
      
      <ApiLoader :loading="blogStore.loading" :fullscreen="true" />
      
      <template v-if="blog">
        <div class="blog-container card">
          <div class="blog-header">
            <h1 class="blog-title">{{ blog.title }}</h1>
            <div class="blog-meta">
              <div class="blog-time">{{ formattedTime }}</div>
              <ThumbButton 
                :blog-id="blog.id" 
                :count="blog.thumbCount" 
                :has-thumb="blog.hasThumb" 
              />
            </div>
          </div>
          
          <div class="blog-cover">
            <img :src="blog.coverImg" :alt="blog.title" class="cover-img" referrerpolicy="no-referrer" />
          </div>
          
          <div class="blog-content">
            <p>{{ blog.content }}</p>
          </div>
        </div>
      </template>
      
      <template v-else-if="!blogStore.loading">
        <div class="empty-state">
          <p>博客不存在或已被删除</p>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import ThumbButton from '../components/ThumbButton.vue';
import LoginBadge from '../components/common/LoginBadge.vue';
import ApiLoader from '../components/common/ApiLoader.vue';
import { useBlogStore } from '../stores/blog';
import { useUserStore } from '../stores/user';
import { formatDate } from '../utils';

const props = defineProps({
  id: {
    type: String,
    required: true
  }
});

const route = useRoute();
const router = useRouter();
const blogStore = useBlogStore();
const userStore = useUserStore();

// 获取博客ID
const blogId = computed(() => String(props.id));

// 获取博客详情
const blog = computed(() => {
  return blogStore.getBlogById(blogId.value);
});

// 格式化时间
const formattedTime = computed(() => {
  if (!blog.value) return '';
  return formatDate(blog.value.createTime);
});

// 获取博客数据
const fetchBlogDetail = async () => {
  await blogStore.fetchBlogDetail(blogId.value);
};

// 返回上一页
const goBack = () => {
  router.back();
};

// 初始化
onMounted(async () => {
  // 尝试恢复登录状态
  await userStore.restoreLogin();
  // 获取博客详情
  await fetchBlogDetail();
});

// 监听博客ID变化，重新获取数据
watch(() => blogId.value, async (newId) => {
  if (newId) {
    await fetchBlogDetail();
  }
});
</script>

<style scoped>
.blog-detail-page {
  @apply min-h-screen bg-gray-50;
}

.page-header {
  @apply flex justify-between items-center mb-8;
}

.back-button {
  @apply flex items-center text-gray-600 hover:text-primary transition-colors;
}

.back-icon {
  @apply w-5 h-5 mr-1;
}

.empty-state {
  @apply flex items-center justify-center py-16 text-gray-500 text-lg;
}

.blog-container {
  @apply p-6 mb-8;
}

.blog-header {
  @apply mb-6;
}

.blog-title {
  @apply text-3xl font-bold text-gray-800 mb-4;
}

.blog-meta {
  @apply flex justify-between items-center;
}

.blog-time {
  @apply text-sm text-gray-500;
}

.blog-cover {
  @apply mb-6 overflow-hidden rounded-lg;
}

.cover-img {
  @apply w-full object-contain;
  max-height: 500px;
}

.blog-content {
  @apply text-gray-700 leading-relaxed whitespace-pre-line;
}
</style> 