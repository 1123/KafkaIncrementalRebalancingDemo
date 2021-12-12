package io.confluent.demos.incrementalrebalancing.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@EnableKafka
@SpringBootApplication
@EnableScheduling
@Slf4j
public class IncrementalRebalancingDemoProducer {

	public static void main(String[] args) {
		SpringApplication.run(IncrementalRebalancingDemoProducer.class, args);
	}

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Scheduled(initialDelay = 100, fixedDelay = 100)
	public void produce() {
		log.info("Sending some data");
		kafkaTemplate.send("test-topic", UUID.randomUUID().toString(), new SimpleDateFormat().format(new Date()));
	}
}


