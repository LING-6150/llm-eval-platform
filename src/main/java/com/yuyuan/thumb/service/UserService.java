package com.yuyuan.thumb.service;

import com.yuyuan.thumb.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author pine
 */
public interface UserService extends IService<User> {

    User getLoginUser(HttpServletRequest request);
}
