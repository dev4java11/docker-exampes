package pe.com.dev4java11;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class Ms5Application {
	
	public static final Random rnd = new Random();
	
	public static final String [] messages = new String[] {
			"Hello", "Wake up", "Silent please", "Let's go", "Move on the top", 
			"Get the money", "Do it", "Yes sir"
	};

	public static void main(String[] args) {
		SpringApplication.run(Ms5Application.class, args);
	}

	@Bean
	public Consumer<String> logPhrases(){
		return x -> {
			log.info("receive phrase {}", x);
		};
	}
	
	@Bean
	public Supplier<String> publisher() {
		return () -> messages[rnd.nextInt(messages.length)];
	}
	
	@Bean
	public Consumer<String> subscriber() {
		return msg -> log.info("message: {}", msg);
	}
}
