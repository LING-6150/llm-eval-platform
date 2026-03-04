package com.yuyuan.thumb.listener.eval;

import com.yuyuan.thumb.listener.thumb.msg.EvalEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Message;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EvalDlqConsumer {

    @PulsarListener(topics = "eval-dlq-topic")
    public void consumeDlq(Message<EvalEvent> message) {
        log.error("Eval task permanently failed, messageId={}, payload={}",
                message.getMessageId(), message.getValue());
        // 生产环境：发告警通知、写失败记录等
    }
}