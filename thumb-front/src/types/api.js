/**
 * @typedef {Object} User
 * @property {string} id - 用户ID
 * @property {string} username - 用户名
 */

/**
 * @typedef {Object} BlogVO
 * @property {string} id - 博客ID
 * @property {string} title - 博客标题
 * @property {string} coverImg - 封面图片URL
 * @property {string} content - 博客内容
 * @property {number} thumbCount - 点赞数
 * @property {string} createTime - 创建时间
 * @property {boolean} hasThumb - 当前用户是否已点赞
 */

/**
 * @typedef {Object} BaseResponse
 * @property {number} code - 响应码
 * @property {*} data - 响应数据
 * @property {string} message - 响应消息
 */

/**
 * @typedef {Object} BaseResponseBoolean
 * @property {number} code - 响应码
 * @property {boolean} data - 布尔类型响应数据
 * @property {string} message - 响应消息
 */

/**
 * @typedef {Object} BaseResponseUser
 * @property {number} code - 响应码
 * @property {User} data - 用户数据
 * @property {string} message - 响应消息
 */

/**
 * @typedef {Object} BaseResponseBlogVO
 * @property {number} code - 响应码
 * @property {BlogVO} data - 博客数据
 * @property {string} message - 响应消息
 */

/**
 * @typedef {Object} BaseResponseListBlogVO
 * @property {number} code - 响应码
 * @property {BlogVO[]} data - 博客列表数据
 * @property {string} message - 响应消息
 */

/**
 * @typedef {Object} DoThumbRequest
 * @property {string} blogId - 博客ID
 */

export {};
 