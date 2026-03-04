package com.yuyuan.thumb.listener.eval;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuyuan.thumb.listener.thumb.msg.EvalEvent;
import com.yuyuan.thumb.model.entity.EvalTask;
import com.yuyuan.thumb.service.EvalTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.SubscriptionType;
import org.apache.pulsar.common.schema.SchemaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EvalConsumer {

    private final EvalTaskService evalTaskService;

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${openai.api-key}")
    private String openaiApiKey;

    @PulsarListener(
            subscriptionName = "eval-subscription",
            topics = "eval-topic",
            schemaType = SchemaType.JSON,
            batch = true,
            subscriptionType = SubscriptionType.Shared
    )
    public void processBatch(List<Message<EvalEvent>> messages) {
        log.info("EvalConsumer processBatch: {}", messages.size());

        for (Message<EvalEvent> message : messages) {
            EvalEvent event = message.getValue();
            if (event == null) continue;

            EvalTask task = evalTaskService.getById(event.getTaskId());
            if (task == null) continue;

            task.setStatus("processing");
            evalTaskService.updateById(task);

            try {
                long startTime = System.currentTimeMillis();
                String result = callLLM(event.getPromptText(), event.getModelName());
                long latency = System.currentTimeMillis() - startTime;

                task.setStatus("completed");
                task.setResultText(result);
                task.setLatency(latency);
                task.setTokenCount(estimateTokens(event.getPromptText(), result));
                evalTaskService.updateById(task);

                log.info("Eval task {} completed, latency={}ms", event.getTaskId(), latency);

            } catch (Exception e) {
                log.error("Eval task {} failed: {}", event.getTaskId(), e.getMessage());
                task.setStatus("failed");
                task.setErrorMsg(e.getMessage());
                evalTaskService.updateById(task);
            }
        }
    }

    private String callLLM(String promptText, String modelName) throws Exception {
        return switch (modelName) {
            case "gpt-3.5-turbo", "gpt-4" -> callOpenAI(promptText, modelName);
            default -> callDeepSeek(promptText);
        };
    }
    private String callDeepSeek(String promptText) throws Exception {
        String requestBody = """
                {
                    "model": "deepseek-chat",
                    "messages": [{"role": "user", "content": "%s"}],
                    "max_tokens": 1024
                }
                """.formatted(promptText.replace("\"", "\\\""));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.deepseek.com/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("DeepSeek API error: " + response.statusCode());
        }

        String body = response.body();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(body);
        return root.path("choices").get(0).path("message").path("content").asText();
    }


    private String callOpenAI(String promptText, String modelName) throws Exception {
        String requestBody = """
            {
                "model": "%s",
                "messages": [{"role": "user", "content": "%s"}],
                "max_tokens": 1024
            }
            """.formatted(modelName, promptText.replace("\"", "\\\""));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + openaiApiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("OpenAI API error: " + response.statusCode()
                    + " " + response.body());
        }

        String body = response.body();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(body);
        return root.path("choices").get(0).path("message").path("content").asText();
    }

    private int estimateTokens(String prompt, String result) {
        return (prompt.length() + result.length()) / 4;
    }

}