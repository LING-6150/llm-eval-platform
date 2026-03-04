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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
    @GetMapping("/stats")
    public BaseResponse<Map<String, Object>> getStats(HttpServletRequest httpRequest) {
        userService.getLoginUser(httpRequest);

        List<EvalTask> allTasks = evalTaskService.list(
                new LambdaQueryWrapper<EvalTask>()
                        .eq(EvalTask::getStatus, "completed")
        );

        // 按模型分组统计
        Map<String, List<EvalTask>> byModel = allTasks.stream()
                .collect(Collectors.groupingBy(EvalTask::getModelName));

        // 模型单价（每1000 tokens的美元价格）
        Map<String, Double> modelPricing = Map.of(
                "deepseek-chat", 0.001,
                "gpt-3.5-turbo", 0.002,
                "gpt-4", 0.03
        );

        List<Map<String, Object>> modelStats = byModel.entrySet().stream()
                .map(entry -> {
                    String model = entry.getKey();
                    List<EvalTask> tasks = entry.getValue();

                    double avgLatency = tasks.stream()
                            .mapToLong(EvalTask::getLatency)
                            .average().orElse(0);

                    long totalTokens = tasks.stream()
                            .mapToInt(EvalTask::getTokenCount)
                            .sum();

                    double price = modelPricing.getOrDefault(model, 0.001);
                    double estimatedCost = (totalTokens / 1000.0) * price;

                    Map<String, Object> stat = new HashMap<>();
                    stat.put("modelName", model);
                    stat.put("taskCount", tasks.size());
                    stat.put("avgLatencyMs", Math.round(avgLatency));
                    stat.put("totalTokens", totalTokens);
                    stat.put("estimatedCostUSD", Math.round(estimatedCost * 10000.0) / 10000.0);
                    return stat;
                })
                .collect(Collectors.toList());

        // 总体统计
        Map<String, Object> result = new HashMap<>();
        result.put("totalTasks", allTasks.size());
        result.put("totalTokens", allTasks.stream().mapToInt(EvalTask::getTokenCount).sum());
        result.put("modelStats", modelStats);

        return ResultUtils.success(result);
    }

}