import { customRef } from 'vue';

/**
 * 格式化日期
 * @param {string|Date} date 日期字符串或Date对象
 * @param {string} format 格式化模板
 * @returns {string} 格式化后的日期字符串
 */
export function formatDate(date, format = 'YYYY-MM-DD HH:mm') {
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  const hours = String(d.getHours()).padStart(2, '0');
  const minutes = String(d.getMinutes()).padStart(2, '0');
  
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes);
}

/**
 * 防抖函数
 * @param {Function} fn 需要防抖的函数
 * @param {number} delay 延迟时间
 * @returns {Function} 防抖处理后的函数
 */
export function debounce(fn, delay = 300) {
  let timeout = null;
  
  return function(...args) {
    if (timeout) {
      clearTimeout(timeout);
    }
    
    timeout = setTimeout(() => {
      fn.apply(this, args);
      timeout = null;
    }, delay);
  };
}

/**
 * 创建防抖ref
 * @param {*} value 初始值
 * @param {number} delay 延迟时间
 * @returns {Ref} 防抖ref
 */
export function useDebouncedRef(value, delay = 300) {
  let timeout;
  return customRef((track, trigger) => {
    return {
      get() {
        track();
        return value;
      },
      set(newValue) {
        clearTimeout(timeout);
        timeout = setTimeout(() => {
          value = newValue;
          trigger();
        }, delay);
      }
    };
  });
}

/**
 * 截取字符串并添加省略号
 * @param {string} str 原字符串
 * @param {number} length 截取长度
 * @returns {string} 截取后的字符串
 */
export function truncateString(str, length = 100) {
  if (!str) return '';
  if (str.length <= length) return str;
  
  return str.substring(0, length) + '...';
}

/**
 * 格式化数字（例如：点赞数）
 * @param {number} num 数字
 * @returns {string} 格式化后的字符串
 */
export function formatNumber(num) {
  if (num >= 1000000) {
    return (num / 1000000).toFixed(1) + 'M';
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K';
  }
  return num.toString();
} 