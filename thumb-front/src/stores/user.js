import { defineStore } from 'pinia';
import { userApi } from '../api/services';

export const useUserStore = defineStore('user', {
  state: () => ({
    // 用户信息
    currentUser: null,
    // 登录状态
    isLoggedIn: false,
  }),
  
  getters: {
    getUserId: (state) => state.currentUser?.id,
    getUsername: (state) => state.currentUser?.username,
  },
  
  actions: {
    /**
     * 登录
     * @param {string} userId 用户ID
     */
    async login(userId) {
      try {
        const response = await userApi.login(userId);
        // 根据接口文档，用户登录接口返回BaseResponseUser类型
        if (response.data && response.data.code === 0 && response.data.data) {
          this.currentUser = response.data.data;
          this.isLoggedIn = true;
          // 保存到本地存储
          localStorage.setItem('userId', userId);
          return true;
        }
        return false;
      } catch (error) {
        console.error('登录失败', error);
        return false;
      }
    },
    
    /**
     * 获取当前登录用户
     */
    async fetchCurrentUser() {
      try {
        const response = await userApi.getLoginUser();
        // 同样使用BaseResponseUser结构
        if (response.data && response.data.code === 0 && response.data.data) {
          this.currentUser = response.data.data;
          this.isLoggedIn = true;
          return true;
        }
        return false;
      } catch (error) {
        console.error('获取用户信息失败', error);
        return false;
      }
    },
    
    /**
     * 清除用户信息（登出）
     */
    clearUser() {
      this.currentUser = null;
      this.isLoggedIn = false;
      localStorage.removeItem('userId');
    },
    
    /**
     * 恢复登录状态（页面刷新后）
     */
    async restoreLogin() {
      const userId = localStorage.getItem('userId');
      if (userId) {
        return await this.login(userId);
      }
      return false;
    },
  },
}); 