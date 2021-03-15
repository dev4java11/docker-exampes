package pe.com.dev4java11;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
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
				streaming.send("filterGreaterEqualSalary2000-out-0", e);
			}
		};
	}
	
	@RestController
	@RequestMapping("/api")
	@AllArgsConstructor(onConstructor_ = @Autowired)
	public class Api {
		
		private StreamBridge bridge;
		
		@PostMapping("/ingest/single")
		public ResponseEntity<?> ingest(@RequestBody Employee person){
			log.info("ingest employee: {}", person);
			bridge.send("employee-out-0", person);
			return ResponseEntity.ok("");
		}
		
		@PostMapping("/ingest/list")
		public ResponseEntity<?> ingestList(@RequestBody List<Employee> persons){
			persons.forEach(p -> ingest(p));
			return ResponseEntity.ok("");
		}
	}
}
