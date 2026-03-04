package com.yuyuan.thumb.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("eval_task")
public class EvalTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String promptText;

    private String modelName;

    private String status;

    private Integer tokenCount;

    private Long latency;

    private String resultText;

    private String errorMsg;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}