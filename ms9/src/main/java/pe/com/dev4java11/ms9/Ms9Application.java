package pe.com.dev4java11.ms9;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.com.dev4java11.ms9.dto.City;
import pe.com.dev4java11.ms9.dto.DeleteCity;

@SpringBootApplication
@Slf4j
public class Ms9Application {
	
	public static String MV_FILTER_DELETED_CITIES = "mv-filter-deleted-cities";

	public static void main(String[] args) {
		SpringApplication.run(Ms9Application.class, args);
	}
	
//	@Bean
//	public Serde<City> citySerde() {
//		return new JsonSerde<>();
//	}
//	 
//	@Bean
//	public StoreBuilder<?> mvFilterDeletedCities() {
//		return Stores.keyValueStoreBuilder(
//				Stores.persistentKeyValueStore(MV_FILTER_DELETED_CITIES),
//				Serdes.String(),
//				citySerde());
//	}
	
	@Bean
	public BiFunction<KStream<UUID, City>, KStream<UUID, DeleteCity>, KStream<UUID, City>> filterDeletedCities() {
		return (cities, deleted) -> 
				cities.toTable()
					.leftJoin(deleted.toTable(),
							(city, delete) -> delete == null ? city : new City(delete.getId(), "TO_DELETE"))
					.filterNot((k, v) -> v.getName().equalsIgnoreCase("TO_DELETE"))
					.toStream()
					.peek((k, v) -> log.info("filter {} - {}", k, v));
	}
	
//	@Bean
//	public Consumer<KTable<UUID, City>> logfilterDeletedCities() {
//		return cities -> 
//				cities
//				.toStream()
//				.foreach((k, v) -> log.info("log filter {} - {}", k, v));
//	}

	@RestController
	@RequestMapping("/api")
	@AllArgsConstructor(onConstructor_ = @Autowired)
	public class ApiController {
		
		private StreamBridge bridge;
		
		private InteractiveQueryService query;
		
		@PostMapping("/cities")
		public ResponseEntity<?> create(@RequestBody City city) {
			log.info("create {}", city);
			return ResponseEntity
					.ok(bridge.send("cout0", MessageBuilder
							.withPayload(city)
							.setHeader(KafkaHeaders.MESSAGE_KEY, city.getId())
							.build()));
		}
		
		@DeleteMapping("/cities/{id}")
		public ResponseEntity<?> delete(@PathVariable UUID id){
			log.info("delete city {}", id);
			return ResponseEntity
					.ok(bridge.send("dcout0", MessageBuilder
							.withPayload(new DeleteCity(id))
							.setHeader(KafkaHeaders.MESSAGE_KEY, id)
							.build()));
		}
		
		@GetMapping("/cities/deleted")
		public ResponseEntity<?> getDeletedCities() {
			Map<Object, Object> map = new LinkedHashMap<>();
			ReadOnlyKeyValueStore<?, ?> data = query.getQueryableStore(MV_FILTER_DELETED_CITIES, QueryableStoreTypes.keyValueStore());
			data.all().forEachRemaining(kv -> map.put(kv.key, kv.value));
			return ResponseEntity.ok(map);
		}
	}
}
