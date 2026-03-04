import { defineStore } from 'pinia';
import { blogApi } from '../api/services';

export const useBlogStore = defineStore('blog', {
  state: () => ({
    // 博客列表
    blogs: [],
    // 博客详情缓存，以博客ID为key
    blogDetails: {},
    // 加载状态
    loading: false,
    // 博客列表是否已加载
    isLoaded: false,
  }),
  
  getters: {
    getBlogById: (state) => (id) => {
      // 将ID统一转为字符串比较，避免数字类型和字符串类型不匹配
      const stringId = String(id);
      // 先从详情缓存中查找
      if (state.blogDetails[stringId]) {
        return state.blogDetails[stringId];
      }
      // 再从列表中查找
      return state.blogs.find(blog => String(blog.id) === stringId);
    },
  },
  
  actions: {
    /**
     * 获取博客列表
     */
    async fetchBlogs() {
      if (this.isLoaded && this.blogs.length > 0) {
        return this.blogs;
      }
      
      this.loading = true;
      try {
        const response = await blogApi.getList();
        if (response.data && response.data.code === 0) {
          this.blogs = response.data.data || [];
          this.isLoaded = true;
        }
        return this.blogs;
      } catch (error) {
        console.error('获取博客列表失败', error);
        return [];
      } finally {
        this.loading = false;
      }
    },
    
    /**
     * 获取博客详情
     * @param {string|number} blogId 博客ID
     */
    async fetchBlogDetail(blogId) {
      if (this.blogDetails[blogId]) {
        return this.blogDetails[blogId];
      }
      
      this.loading = true;
      try {
        const response = await blogApi.getDetail(blogId);
        if (response.data && response.data.code === 0 && response.data.data) {
          const blog = response.data.data;
          // 使用字符串ID作为键
          this.blogDetails[String(blogId)] = blog;
          return blog;
        }
        return null;
      } catch (error) {
        console.error('获取博客详情失败', error);
        return null;
      } finally {
        this.loading = false;
      }
    },
    
    /**
     * 记录博客访问历史
     * @param {number} blogId 博客ID
     */
    addToHistory(blogId) {
      const history = JSON.parse(localStorage.getItem('blogHistory') || '[]');
      
      // 如果已存在，移除旧记录
      const index = history.findIndex(id => id === blogId);
      if (index !== -1) {
        history.splice(index, 1);
      }
      
      // 添加到最前面
      history.unshift(blogId);
      
      // 只保留最近10条记录
      const recentHistory = history.slice(0, 10);
      localStorage.setItem('blogHistory', JSON.stringify(recentHistory));
    },
    
    /**
     * 获取最近访问的博客ID列表
     */
    getRecentHistory() {
      return JSON.parse(localStorage.getItem('blogHistory') || '[]');
    },
    
    /**
     * 更新博客点赞状态和数量
     * @param {string|number} blogId 博客ID
     * @param {boolean} hasThumb 是否点赞
     */
    updateBlogThumbStatus(blogId, hasThumb) {
      const stringId = String(blogId);
      
      // 更新详情缓存
      if (this.blogDetails[stringId]) {
        const blog = this.blogDetails[stringId];
        blog.hasThumb = hasThumb;
        blog.thumbCount = hasThumb 
          ? blog.thumbCount + 1 
          : Math.max(0, blog.thumbCount - 1);
      }
      
      // 更新列表
      const blogIndex = this.blogs.findIndex(blog => String(blog.id) === stringId);
      if (blogIndex !== -1) {
        const blog = this.blogs[blogIndex];
        blog.hasThumb = hasThumb;
        blog.thumbCount = hasThumb 
          ? blog.thumbCount + 1 
          : Math.max(0, blog.thumbCount - 1);
      }
    },
  },
}); 