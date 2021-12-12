package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Listener {

    @KafkaListener(topics = "test-topic", containerFactory = "concurrentKafkaListenerContainerFactory")
    public void listen(ConsumerRecord<?, ?> cr) {
        log.info(cr.toString());
    }

}
