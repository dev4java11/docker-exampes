package pe.com.dev4java11;

import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.condition.EmbeddedKafkaCondition;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka
class Ms5ApplicationTests {
	
	@Autowired
	StreamBridge bridge;
	
	@BeforeClass
	public static void setup() {
		System.setProperty("spring.cloud.stream.kafka.binder.brokers", EmbeddedKafkaCondition.getBroker().getBrokersAsString());
	}

	@Test
	void contextLoads() {
		bridge.send("logPhrases-out-0", "Hello World.", MediaType.TEXT_PLAIN);
	}

}
