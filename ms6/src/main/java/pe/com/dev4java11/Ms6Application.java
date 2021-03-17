package pe.com.dev4java11;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.binder.kafka.KafkaMessageChannelBinder;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
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
	
	public static final String MV_KSTEAM_COUNT_GENDERS = "mv-kscg";

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
	
	
	@Bean
	public Function<KStream<String, Employee>, KStream<String, Long>> kStreamCountGenders() {
		return stream -> stream
				.map((k, v) -> new KeyValue<>(String.valueOf(v.getGender()), 1L))
				.groupByKey(Grouped.with(Serdes.String(), Serdes.Long()))
				.count(Materialized
						.<String, Long, KeyValueStore<Bytes, byte[]>>as(MV_KSTEAM_COUNT_GENDERS)
						.withKeySerde(Serdes.String())
						.withValueSerde(Serdes.Long()))
				.toStream();
	}
	
	@Bean
	public Consumer<KStream<String, Long>> kStreamLogCountGenders() {
		return stream -> stream
				.foreach((k, v) -> log.info("log count genders: {} - {}", k, v));
	}
	
	@RestController
	@RequestMapping("/api")
	@AllArgsConstructor(onConstructor_ = @Autowired)
	public class Api {
		
		private StreamBridge bridge;
		
		private InteractiveQueryService queryService;
		
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
		
		@GetMapping("/count/genders")
		public ResponseEntity<?> countGenders(){
			Map<Object, Object> map = new LinkedHashMap<>();
			ReadOnlyKeyValueStore<?, ?> query = queryService.getQueryableStore(MV_KSTEAM_COUNT_GENDERS, QueryableStoreTypes.keyValueStore());
			query.all().forEachRemaining(kv -> map.put(kv.key, kv.value));
			return ResponseEntity.ok(map);
		}
	}
}
