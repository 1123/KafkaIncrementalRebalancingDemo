package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

@EnableKafka
@SpringBootApplication
public class IncrementalRebalancingDemoConsumer {

	public static void main(String[] args) {
		SpringApplication.run(IncrementalRebalancingDemoConsumer.class, args);
	}

}


