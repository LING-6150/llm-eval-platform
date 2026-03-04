package com.yuyuan.thumb.demo;

import com.yuyuan.thumb.listener.thumb.msg.ThumbEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.pulsar.core.PulsarTemplate;

@Configuration
@Slf4j
public class PulsarBootHelloWorld {

    // @Bean
    ApplicationRunner runner(PulsarTemplate<ThumbEvent> pulsarTemplate) {
        ThumbEvent thumbEvent = new ThumbEvent();
        for (int i = 0; i < 10000; i++) {
            pulsarTemplate.send("thumb-topic", thumbEvent);
            // ThreadUtil.sleep(500);
            log.info("Pulsar boot-hello-pulsar-topic" + (i + "----------------------------"));
        }
        return e -> {};
    }

    @PulsarListener(subscriptionName = "hello-pulsar-sub", topics = "hello-pulsar-topic")
    void listen(String message) {
        System.out.println("Message Received: " + message);
    }
}