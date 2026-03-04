import { defineStore } from 'pinia';
import { thumbApi } from '../api/services';
import { useBlogStore } from './blog';

export const useThumbStore = defineStore('thumb', {
  state: () => ({
    // 用户已点赞的博客ID集合
    thumbedBlogs: new Set(),
    // 点赞操作状态
    isLoading: false,
  }),
  
  getters: {
    /**
     * 判断博客是否已点赞
     * @param {string|number} blogId - 博客ID
     * @returns {boolean}
     */
    hasThumb: (state) => (blogId) => {
      return state.thumbedBlogs.has(String(blogId));
    }
  },
  
  actions: {
    /**
     * 点赞博客
     * @param {string|number} blogId - 博客ID
     * @returns {Promise<boolean>} 点赞是否成功
     */
    async doThumb(blogId) {
      if (this.isLoading) return false;
      
      this.isLoading = true;
      try {
        console.log('开始点赞操作', blogId);
        const response = await thumbApi.doThumb(blogId);
        
        if (response.data && response.data.code === 0 && response.data.data === true) {
          this.thumbedBlogs.add(String(blogId));
          console.log('点赞成功', blogId);
          
          // 更新博客状态
          const blogStore = useBlogStore();
          blogStore.updateBlogThumbStatus(blogId, true);
          
          return true;
        }
        console.log('点赞失败', response.data);
        return false;
      } catch (error) {
        console.error('点赞操作异常', error);
        return false;
      } finally {
        this.isLoading = false;
      }
    },
    
    /**
     * 取消点赞
     * @param {string|number} blogId - 博客ID
     * @returns {Promise<boolean>} 取消点赞是否成功
     */
    async undoThumb(blogId) {
      if (this.isLoading) return false;
      
      this.isLoading = true;
      try {
        console.log('开始取消点赞操作', blogId);
        const response = await thumbApi.undoThumb(blogId);
        
        if (response.data && response.data.code === 0 && response.data.data === true) {
          this.thumbedBlogs.delete(String(blogId));
          console.log('取消点赞成功', blogId);
          
          // 更新博客状态
          const blogStore = useBlogStore();
          blogStore.updateBlogThumbStatus(blogId, false);
          
          return true;
        }
        console.log('取消点赞失败', response.data);
        return false;
      } catch (error) {
        console.error('取消点赞操作异常', error);
        return false;
      } finally {
        this.isLoading = false;
      }
    },
    
    /**
     * 设置博客的点赞状态（通常在加载博客列表或详情时调用）
     * @param {string|number} blogId - 博客ID
     * @param {boolean} status - 点赞状态
     */
    setThumbStatus(blogId, status) {
      if (status) {
        this.thumbedBlogs.add(String(blogId));
      } else {
        this.thumbedBlogs.delete(String(blogId));
      }
    },
    
    /**
     * 批量设置多个博客的点赞状态
     * @param {Array<{id: number, hasThumb: boolean}>} blogs - 博客数组
     */
    setMultipleThumbStatus(blogs) {
      if (!blogs || !blogs.length) return;
      
      blogs.forEach(blog => {
        this.setThumbStatus(blog.id, blog.hasThumb);
      });
    }
  },
}); 