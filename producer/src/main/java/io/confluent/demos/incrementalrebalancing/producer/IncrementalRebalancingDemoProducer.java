package io.confluent.demos.incrementalrebalancing.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.nio.charset.StandardCharsets;
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

	private final int numMessages;
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final int payloadCharacters;

	@Autowired
	public IncrementalRebalancingDemoProducer(
			@Value("${PRODUCER_NUM_MESSAGES}") int numMessages,
			@Value("${PRODUCER_PAYLOAD_CHARACTERS}") int payloadCharacters,
			KafkaTemplate<String, String> kafkaTemplate) {
		this.numMessages = numMessages;
		this.kafkaTemplate = kafkaTemplate;
		this.payloadCharacters = payloadCharacters;
	}

	@Scheduled(initialDelay = 100, fixedDelayString = "${PRODUCER_FIXED_DELAY_MS}")
	public void produce() {
		String payload = RandomStringUtils.randomAlphabetic(payloadCharacters);
		log.info("Sending {} messages with payload size {}", numMessages, payload.getBytes(StandardCharsets.UTF_8).length);
		for (int i = 0; i < numMessages; i++) {
			kafkaTemplate.send("test-topic", UUID.randomUUID().toString(), payload);
		}
	}
}


