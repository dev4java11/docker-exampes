package pe.com.dev4java11;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(ports = 9092, topics = {"log.phrases"}, partitions = 1)
class Ms5ApplicationTests {
	
	@Autowired
	StreamBridge bridge;

	@Test
	void contextLoads() {
		bridge.send("logPhrases-out-0", "Hello World.", MediaType.TEXT_PLAIN);
	}

}
