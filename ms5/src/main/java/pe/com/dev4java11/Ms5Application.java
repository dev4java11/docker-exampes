package pe.com.dev4java11;

import java.util.function.Consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class Ms5Application {

	public static void main(String[] args) {
		SpringApplication.run(Ms5Application.class, args);
	}

	@Bean
	public Consumer<String> logPhrases(){
		return x -> {
			log.info("receive phrase {}", x);
		};
	}
}
