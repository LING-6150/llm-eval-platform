package com.yuyuan.thumb.model.dto.eval;

import lombok.Data;

@Data
public class SubmitEvalRequest {
    private String promptText;
    private String modelName;
}