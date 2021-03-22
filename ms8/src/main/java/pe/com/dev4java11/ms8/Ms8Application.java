package pe.com.dev4java11.ms8;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.com.dev4java11.ms8.dto.Notification;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.EmitFailureHandler;

@SpringBootApplication
@Slf4j
public class Ms8Application {

	public static void main(String[] args) {
		SpringApplication.run(Ms8Application.class, args);
	}
	
	@Bean
	public Sinks.Many<Notification> sinkApi() {
		return Sinks.many().unicast().onBackpressureBuffer();
	}
	
	@Bean
	public Consumer<Notification> notificationApi() {
		return notification -> {
			log.info("consumed notification {}", notification);
			sinkApi().emitNext(notification, EmitFailureHandler.FAIL_FAST);
		};
	}

	@RestController
	@RequestMapping("/api")
	@AllArgsConstructor(onConstructor_ = @Autowired)
	public class ApiController {
		
		@Qualifier("sinkApi")
		private Sinks.Many<Notification> sub;
		
		private StreamBridge bridge;
		
		@PostMapping("/")
		public ResponseEntity<?> create(@RequestBody Notification notification) {
			boolean result = bridge.send("nout0", MessageBuilder
					.withPayload(notification)
					.setHeader(KafkaHeaders.MESSAGE_KEY, notification.getId())
					.build());
			log.info("sended {} {}", result, notification);
			return result ? 
					ResponseEntity.ok().build() : 
					ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		@GetMapping(path = "/flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
		public Flux<?> getFlux(){
			return sub.asFlux()
					.map(notification -> ServerSentEvent
							.builder(notification)
							.id(notification.getId().toString())
							.build())
					.log();
		}
	}
}
