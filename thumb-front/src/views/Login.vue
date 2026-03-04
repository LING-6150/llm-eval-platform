<template>
    <div class="login-page">
      <div class="container-custom py-16">
        <div class="login-card card">
          <h1 class="login-title">用户登录</h1>
          
          <div class="login-form">
            <div class="form-group">
              <label for="userId" class="form-label">用户ID</label>
              <input 
                v-model="userId" 
                type="text" 
                id="userId" 
                class="form-input" 
                placeholder="请输入用户ID"
                @keyup.enter="handleLogin"
              />
            </div>
            
            <div class="form-actions">
              <button @click="goBack" class="btn btn-outline">返回</button>
              <button @click="handleLogin" class="btn btn-primary" :disabled="!userId || isLoading">
                {{ isLoading ? '登录中...' : '登录' }}
              </button>
            </div>
            
            <div v-if="loginError" class="login-error">
              {{ loginError }}
            </div>
            
            <div class="login-tips">
              <p>提示：输入任意数字ID进行登录</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  import { useUserStore } from '../stores/user';
  
  const router = useRouter();
  const userStore = useUserStore();
  
  const userId = ref('');
  const isLoading = ref(false);
  const loginError = ref('');
  
  // 处理登录
  const handleLogin = async () => {
    // 清除错误信息
    loginError.value = '';
    
    // 表单验证
    if (!userId.value) {
      loginError.value = '请输入用户ID';
      return;
    }
    
    // 登录操作
    isLoading.value = true;
    try {
      console.log('正在尝试登录，用户ID:', userId.value);
      const success = await userStore.login(userId.value);
      console.log('登录结果:', success, '用户信息:', userStore.currentUser);
      if (success) {
        // 登录成功，跳转到首页
        router.push('/');
      } else {
        loginError.value = '登录失败，请稍后重试';
      }
    } catch (error) {
      console.error('登录异常', error);
      loginError.value = '登录异常，请稍后重试';
    } finally {
      isLoading.value = false;
    }
  };
  
  // 返回上一页
  const goBack = () => {
    router.back();
  };
  </script>
  
  <style scoped>
  .login-page {
    @apply min-h-screen bg-gray-50 flex items-center;
  }
  
  .login-card {
    @apply max-w-md mx-auto p-8;
  }
  
  .login-title {
    @apply text-2xl font-bold text-gray-800 mb-6 text-center;
  }
  
  .login-form {
    @apply space-y-6;
  }
  
  .form-group {
    @apply space-y-2;
  }
  
  .form-label {
    @apply block text-gray-700 font-medium;
  }
  
  .form-input {
    @apply w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent;
  }
  
  .form-actions {
    @apply flex justify-between items-center gap-4 pt-2;
  }
  
  .login-error {
    @apply text-danger text-sm mt-2;
  }
  
  .login-tips {
    @apply text-gray-500 text-sm text-center mt-6;
  }
  </style>