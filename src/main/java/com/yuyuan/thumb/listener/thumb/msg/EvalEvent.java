package com.yuyuan.thumb.listener.thumb.msg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvalEvent {
    private Long taskId;
    private Long userId;
    private String promptText;
    private String modelName;
    private String cacheKey;  // 新增
    private LocalDateTime eventTime;
}