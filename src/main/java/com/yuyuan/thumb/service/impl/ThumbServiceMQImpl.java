package com.yuyuan.thumb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuyuan.thumb.constant.RedisLuaScriptConstant;
import com.yuyuan.thumb.listener.thumb.msg.ThumbEvent;
import com.yuyuan.thumb.mapper.ThumbMapper;
import com.yuyuan.thumb.model.dto.thumb.DoThumbRequest;
import com.yuyuan.thumb.model.entity.Thumb;
import com.yuyuan.thumb.model.entity.User;
import com.yuyuan.thumb.model.enums.LuaStatusEnum;
import com.yuyuan.thumb.service.ThumbService;
import com.yuyuan.thumb.service.UserService;
import com.yuyuan.thumb.util.RedisKeyUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author pine
 */
@Service("thumbService")
@Slf4j
@RequiredArgsConstructor
public class ThumbServiceMQImpl extends ServiceImpl<ThumbMapper, Thumb>
        implements ThumbService {

    private final UserService userService;

    private final RedisTemplate<String, Object> redisTemplate;

    private final PulsarTemplate<ThumbEvent> pulsarTemplate;

    @Override
    public Boolean doThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        if (doThumbRequest == null || doThumbRequest.getBlogId() == null) {
            throw new RuntimeException("参数错误");
        }
        User loginUser = userService.getLoginUser(request);
        Long loginUserId = loginUser.getId();
        Long blogId = doThumbRequest.getBlogId();
        String userThumbKey = RedisKeyUtil.getUserThumbKey(loginUserId);
        // 执行 Lua 脚本，点赞存入 Redis
        long result = redisTemplate.execute(
                RedisLuaScriptConstant.THUMB_SCRIPT_MQ,
                List.of(userThumbKey),
                blogId
        );
        if (LuaStatusEnum.FAIL.getValue() == result) {
            throw new RuntimeException("用户已点赞");
        }

        ThumbEvent thumbEvent = ThumbEvent.builder()
                .blogId(blogId)
                .userId(loginUserId)
                .type(ThumbEvent.EventType.INCR)
                .eventTime(LocalDateTime.now())
                .build();
        pulsarTemplate.sendAsync("thumb-topic", thumbEvent).exceptionally(ex -> {
            redisTemplate.opsForHash().delete(userThumbKey, blogId.toString(), true);
            log.error("点赞事件发送失败: userId={}, blogId={}", loginUserId, blogId, ex);
            return null;
        });

        return true;
    }

    @Override
    public Boolean undoThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        if (doThumbRequest == null || doThumbRequest.getBlogId() == null) {
            throw new RuntimeException("参数错误");
        }
        User loginUser = userService.getLoginUser(request);
        Long loginUserId = loginUser.getId();
        Long blogId = doThumbRequest.getBlogId();
        String userThumbKey = RedisKeyUtil.getUserThumbKey(loginUserId);
        // 执行 Lua 脚本，点赞记录从 Redis 删除
        long result = redisTemplate.execute(
                RedisLuaScriptConstant.UNTHUMB_SCRIPT_MQ,
                List.of(userThumbKey),
                blogId
        );
        if (LuaStatusEnum.FAIL.getValue() == result) {
            throw new RuntimeException("用户未点赞");
        }
        ThumbEvent thumbEvent = ThumbEvent.builder()
                .blogId(blogId)
                .userId(loginUserId)
                .type(ThumbEvent.EventType.DECR)
                .eventTime(LocalDateTime.now())
                .build();
        pulsarTemplate.sendAsync("thumb-topic", thumbEvent).exceptionally(ex -> {
            redisTemplate.opsForHash().put(userThumbKey, blogId.toString(), true);
            log.error("点赞事件发送失败: userId={}, blogId={}", loginUserId, blogId, ex);
            return null;
        });

        return true;
    }

    @Override
    public Boolean hasThumb(Long blogId, Long userId) {
        return redisTemplate.opsForHash().hasKey(RedisKeyUtil.getUserThumbKey(userId), blogId.toString());
    }

}




