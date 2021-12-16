package com.example.demo;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Listener {

    @Value("${MESSAGE_CONSUME_DELAY_MS}")
    private long messageConsumeDelay;

    @SneakyThrows
    @KafkaListener(topics = "test-topic")
    public void listen(ConsumerRecord<?, ?> cr) {
        Thread.sleep(messageConsumeDelay);
        if (cr.offset() % 100 == 0) { log.info(cr.toString()); }
    }

}
