package pe.com.dev4java11;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.binder.kafka.KafkaMessageChannelBinder;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class Ms6Application {
	
	public static final Double SALARY_2000 = Double.valueOf("2000");
	
	public static final Date DATE_1990 = Date
			.from(LocalDate.of(1990, 1, 1)
			.atStartOfDay(ZoneId.systemDefault())
			.toInstant()); 

	public static void main(String[] args) {
		SpringApplication.run(Ms6Application.class, args);
	}
	
	@Autowired
	private StreamBridge streaming;
	
	@Bean
	public Consumer<Employee> filterGreaterEqualSalary2000() {
		return e -> {
			if(e.getSalary().compareTo(SALARY_2000) >= 0) {
				log.info("employee.salary >= 2000: {}", e);
				streaming.send("fges2000out0", 
						MessageBuilder.withPayload(e)
							.setHeader(KafkaHeaders.MESSAGE_KEY, e.getId())
							.build());
			}else {
				throw new RuntimeException("employee " + e.getName() + " has salary is minor to 2000.");
			}
		};
	}
	
	@Bean
	public Consumer<Message<?>> errorFilterGreaterEqualSalary2000() {
		return e -> {
			log.info("headers: {}", e.getHeaders());
			log.info("payload: {}", e.getPayload());
			log.error("error message {}", new String(e.getHeaders().get(KafkaMessageChannelBinder.X_EXCEPTION_MESSAGE, byte[].class), StandardCharsets.UTF_8));
			log.error("error fcqn {}", new String(e.getHeaders().get(KafkaMessageChannelBinder.X_EXCEPTION_FQCN, byte[].class), StandardCharsets.UTF_8));
			log.error("error stacktrace {}", new String(e.getHeaders().get(KafkaMessageChannelBinder.X_EXCEPTION_STACKTRACE, byte[].class), StandardCharsets.UTF_8));
		};
	}
	
	@Bean
	public Function<KStream<String, Employee>, KStream<String, Employee>> kStreamFilterBirthdayGreaterEqual1900() {
		return stream -> stream
				.filter((k, v) -> v.getBirthday().after(DATE_1990));
	}
	
	@RestController
	@RequestMapping("/api")
	@AllArgsConstructor(onConstructor_ = @Autowired)
	public class Api {
		
		private StreamBridge bridge;
		
		@PostMapping("/ingest/single")
		public ResponseEntity<?> ingest(@RequestBody Employee employee){
			log.info("ingest employee: {}", employee);
			bridge.send("empout0", MessageBuilder
					.withPayload(employee)
					.setHeader(KafkaHeaders.MESSAGE_KEY, employee.getId())
					.build());
			return ResponseEntity.ok("");
		}
		
		@PostMapping("/ingest/list")
		public ResponseEntity<?> ingestList(@RequestBody List<Employee> persons){
			persons.forEach(p -> ingest(p));
			return ResponseEntity.ok("");
		}
	}
}
