package com.pe.dev4java11;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
@Testcontainers
class Ms4ApplicationTests {
	
	@Container
	public static KafkaContainer kafka = 
		new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));
	
	@BeforeEach
	public void start() {
		kafka.start();
		log.info("start kafka {}", kafka.getBootstrapServers());
	}

	@Test
	void contextLoads() {
		log.info("executing test");
	}
	
	@AfterEach
	public void stop() {
		kafka.stop();
		log.info("stop kafka {}");
	}

}
