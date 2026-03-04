<template>
  <div class="login-badge">
    <template v-if="userStore.isLoggedIn">
      <div class="user-info">
        <span class="username">{{ userStore.getUsername || '用户' + userStore.getUserId }}</span>
        <button @click="logout" class="logout-btn">退出</button>
      </div>
    </template>
    <template v-else>
      <button @click="goToLogin" class="login-btn">登录</button>
    </template>
  </div>
</template>

<script setup>
import { useUserStore } from '../../stores/user';
import { useRouter } from 'vue-router';
import { onMounted, watch } from 'vue';

const userStore = useUserStore();
const router = useRouter();

onMounted(() => {
  console.log('LoginBadge组件加载，当前用户状态:', {
    isLoggedIn: userStore.isLoggedIn,
    currentUser: userStore.currentUser,
    getUserId: userStore.getUserId,
    getUsername: userStore.getUsername
  });
});

// 监听用户状态变化
watch(() => userStore.isLoggedIn, (newValue) => {
  console.log('用户登录状态变化:', newValue, userStore.currentUser);
});

const goToLogin = () => {
  router.push('/login');
};

const logout = () => {
  userStore.clearUser();
  router.push('/');
};
</script>

<style scoped>
.login-badge {
  @apply flex items-center;
}

.user-info {
  @apply flex items-center gap-2;
}

.username {
  @apply text-gray-800 font-medium;
}

.logout-btn {
  @apply text-sm text-gray-500 hover:text-danger transition-colors;
}

.login-btn {
  @apply btn btn-primary text-sm py-1 px-3;
}
</style> 