package pe.com.dev4java11;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class Ms7ApplicationTests {

	@Test
	void contextLoads() {
		byte[] data = new byte[] {123, 34, 105, 100, 34, 58, 51, 44, 34, 110, 97, 109, 101, 34, 58, 34, 67, 108, 101, 109, 101, 110, 116, 105, 110, 101, 32, 66, 97, 117, 99, 104, 34, 44, 34, 117, 115, 101, 114, 110, 97, 109, 101, 34, 58, 34, 83, 97, 109, 97, 110, 116, 104, 97, 34, 44, 34, 101, 109, 97, 105, 108, 34, 58, 34, 78, 97, 116, 104, 97, 110, 64, 121, 101, 115, 101, 110, 105, 97, 46, 110, 101, 116, 34, 125};
		log.info("data {}", new String(data, StandardCharsets.UTF_8));
	}

}
