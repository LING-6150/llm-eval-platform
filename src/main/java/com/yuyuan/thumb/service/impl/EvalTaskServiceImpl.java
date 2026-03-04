package com.yuyuan.thumb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuyuan.thumb.mapper.EvalTaskMapper;
import com.yuyuan.thumb.model.entity.EvalTask;
import com.yuyuan.thumb.service.EvalTaskService;
import org.springframework.stereotype.Service;

@Service
public class EvalTaskServiceImpl extends ServiceImpl<EvalTaskMapper, EvalTask>
        implements EvalTaskService {
}