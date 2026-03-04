import { get, post } from './request';

/**
 * 博客相关API
 */
export const blogApi = {
  /**
   * 获取博客列表
   * @returns {Promise<import('../types/api').BaseResponseListBlogVO>}
   */
  getList() {
    return get('/blog/list');
  },

  /**
   * 获取博客详情
   * @param {string|number} blogId - 博客ID
   * @returns {Promise<import('../types/api').BaseResponseBlogVO>}
   */
  getDetail(blogId) {
    const stringBlogId = String(blogId);
    return get('/blog/get', { blogId: stringBlogId });
  },
};

/**
 * 点赞相关API
 */
export const thumbApi = {
  /**
   * 点赞博客
   * @param {string|number} blogId - 博客ID
   * @returns {Promise<import('../types/api').BaseResponseBoolean>}
   */
  doThumb(blogId) {
    const stringBlogId = String(blogId);
    console.log('调用点赞API：', `/thumb/do`, { blogId: stringBlogId });
    return post('/thumb/do', { blogId: stringBlogId })
      .then(response => {
        console.log('点赞API响应：', response);
        return response;
      })
      .catch(error => {
        console.error('点赞API错误：', error);
        throw error;
      });
  },

  /**
   * 取消点赞
   * @param {string|number} blogId - 博客ID
   * @returns {Promise<import('../types/api').BaseResponseBoolean>}
   */
  undoThumb(blogId) {
    const stringBlogId = String(blogId);
    console.log('调用取消点赞API：', `/thumb/undo`, { blogId: stringBlogId });
    return post('/thumb/undo', { blogId: stringBlogId })
      .then(response => {
        console.log('取消点赞API响应：', response);
        return response;
      })
      .catch(error => {
        console.error('取消点赞API错误：', error);
        throw error;
      });
  },
};

/**
 * 用户相关API
 */
export const userApi = {
  /**
   * 用户登录
   * @param {string} userId - 用户ID
   * @returns {Promise<import('../types/api').BaseResponseUser>}
   */
  login(userId) {
    const stringUserId = String(userId);
    console.log('调用登录API：', `/user/login?userId=${stringUserId}`);
    return get('/user/login', { userId: stringUserId })
      .then(response => {
        console.log('登录API响应：', response);
        return response;
      })
      .catch(error => {
        console.error('登录API错误：', error);
        throw error;
      });
  },

  /**
   * 获取当前登录用户
   * @returns {Promise<import('../types/api').BaseResponseUser>}
   */
  getLoginUser() {
    console.log('调用获取当前用户API');
    return get('/user/get/login')
      .then(response => {
        console.log('获取当前用户API响应：', response);
        return response;
      })
      .catch(error => {
        console.error('获取当前用户API错误：', error);
        throw error;
      });
  },
}; 