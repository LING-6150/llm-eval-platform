package com.yuyuan.thumb.model.enums;

import lombok.Getter;

/**
 * 点赞类型
 *
 * @author pine
 */
@Getter
public enum ThumbTypeEnum {
    // 点赞
    INCR(1),
    // 取消点赞
    DECR(-1),
    // 不发生改变
    NON(0),
    ;

    private final int value;

    ThumbTypeEnum(int value) {
        this.value = value;
    }

}
