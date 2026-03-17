package com.yuyuan.thumb.model.dto.eval;

import lombok.Data;
import java.util.List;

@Data
public class BatchEvalRequest {
    // 多个prompt
    private List<String> promptTexts;
    // 多个模型
    private List<String> modelNames;
}
