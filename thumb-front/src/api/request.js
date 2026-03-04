import axios from 'axios';
import { useUserStore } from '../stores/user';

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  withCredentials: true,  // 允许跨域请求携带cookie
  // 配置参数序列化器，防止大整数精度丢失
  paramsSerializer: {
    serialize: (params) => {
      return Object.keys(params).map(key => {
        // 将所有参数值作为字符串处理
        const value = String(params[key]);
        return `${encodeURIComponent(key)}=${encodeURIComponent(value)}`;
      }).join('&');
    }
  },
  // 设置referrer策略为no-referrer
  referrerPolicy: 'no-referrer',
  // 设置请求头
  headers: {
    'Referrer-Policy': 'no-referrer'
  }
});

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 可以在这里添加loading状态
    document.body.classList.add('api-loading');
    return config;
  },
  (error) => {
    document.body.classList.remove('api-loading');
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    document.body.classList.remove('api-loading');
    return response;
  },
  (error) => {
    document.body.classList.remove('api-loading');
    
    // 错误处理
    if (error.response) {
      const { status } = error.response;
      
      // 处理401未授权错误
      if (status === 401) {
        const userStore = useUserStore();
        userStore.clearUser();
        // 可以在这里添加提示或跳转登录页面
        window.location.href = '/login';
      }
    }
    
    return Promise.reject(error);
  }
);

// 封装请求方法
export const get = (url, params = {}) => {
  // 确保所有参数都是字符串类型
  const stringParams = {};
  for (const key in params) {
    if (params.hasOwnProperty(key)) {
      stringParams[key] = String(params[key]);
    }
  }
  
  return request({
    method: 'get',
    url,
    params: stringParams,
  });
};

export const post = (url, data = {}) => {
  // 确保所有数据都是字符串类型
  const stringData = {};
  for (const key in data) {
    if (data.hasOwnProperty(key)) {
      stringData[key] = String(data[key]);
    }
  }
  
  return request({
    method: 'post',
    url,
    data: stringData,
  });
};

export default request; 