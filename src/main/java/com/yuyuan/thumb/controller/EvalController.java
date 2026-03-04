package com.yuyuan.thumb.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuyuan.thumb.common.BaseResponse;
import com.yuyuan.thumb.common.ResultUtils;
import com.yuyuan.thumb.listener.thumb.msg.EvalEvent;
import com.yuyuan.thumb.model.dto.eval.SubmitEvalRequest;
import com.yuyuan.thumb.model.entity.EvalTask;
import com.yuyuan.thumb.model.entity.User;
import com.yuyuan.thumb.service.EvalTaskService;
import com.yuyuan.thumb.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/eval")
@RequiredArgsConstructor
@Slf4j
public class EvalController {

    private final EvalTaskService evalTaskService;
    private final UserService userService;
    private final PulsarTemplate<EvalEvent> pulsarTemplate;

    @PostMapping("/submit")
    public BaseResponse<Long> submitEval(
            @RequestBody SubmitEvalRequest request,
            HttpServletRequest httpRequest) {

        User loginUser = userService.getLoginUser(httpRequest);

        // 创建任务记录
        EvalTask task = new EvalTask();
        task.setUserId(loginUser.getId());
        task.setPromptText(request.getPromptText());
        task.setModelName(request.getModelName() != null
                ? request.getModelName() : "deepseek-chat");
        task.setStatus("pending");
        evalTaskService.save(task);

        // 发送到Pulsar
        EvalEvent event = EvalEvent.builder()
                .taskId(task.getId())
                .userId(loginUser.getId())
                .promptText(request.getPromptText())
                .modelName(task.getModelName())
                .eventTime(LocalDateTime.now())
                .build();

        pulsarTemplate.sendAsync("eval-topic", event).exceptionally(ex -> {
            log.error("EvalEvent send failed: taskId={}", task.getId(), ex);
            task.setStatus("failed");
            task.setErrorMsg("Failed to queue task");
            evalTaskService.updateById(task);
            return null;
        });

        return ResultUtils.success(task.getId());
    }

    @GetMapping("/list")
    public BaseResponse<List<EvalTask>> listMyTasks(HttpServletRequest httpRequest) {
        User loginUser = userService.getLoginUser(httpRequest);
        LambdaQueryWrapper<EvalTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EvalTask::getUserId, loginUser.getId())
                .orderByDesc(EvalTask::getCreateTime);
        return ResultUtils.success(evalTaskService.list(wrapper));
    }

    @GetMapping("/task/{taskId}")
    public BaseResponse<EvalTask> getTask(@PathVariable Long taskId,
                                          HttpServletRequest httpRequest) {
        userService.getLoginUser(httpRequest);
        EvalTask task = evalTaskService.getById(taskId);
        return ResultUtils.success(task);
    }
}