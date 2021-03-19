package pe.com.dev4java11;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import pe.com.dev4java11.jsonplaceholder.Comment;
import pe.com.dev4java11.jsonplaceholder.Post;
import pe.com.dev4java11.jsonplaceholder.User;
import pe.com.dev4java11.materialized.MaterializedPostsAndComments;
import pe.com.dev4java11.materialized.MaterializedUsersAndPosts;
import pe.com.dev4java11.util.JsonPlaceholderConfig;

@SpringBootApplication
@Slf4j
public class Ms7Application {
	
	public static final String MV_POST = "mv-post";
	
	@Autowired
	private JsonPlaceholderConfig config;
	
	@Autowired
	private StreamBridge bridge;
	
	@Autowired
	private RestTemplate template;

	public static void main(String[] args) {
		SpringApplication.run(Ms7Application.class, args);
	}
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Bean
	public Function<KStream<Long, User>, KTable<Long, User>> usersToKTable(){
		return users -> users
						.peek((k, v) -> log.info("user [key {} - data {}]", k, v))
						.toTable();
	}
	
	@Bean
	public Function<KStream<Long, Post>, KTable<Long, Post>> postsToKTable(){
		return posts -> posts
						.peek((k, v) -> log.info("post [key {} - data {}]", k, v))
						.toTable();
	}
	
	@Bean
	public Function<KStream<Long, Comment>, KTable<Long, Comment>> commentsToKTable(){
		return comments -> comments
						.peek((k, v) -> log.info("comment [key {} - data {}]", k, v))
						.toTable();
	}
	
	
	@Bean
	public BiConsumer<KTable<Long, User>, KTable<Long, Post>> materializedUsersAndPosts() {
		return (users, posts) -> {
			posts
			.join(users,
				Post::getUserId, 
				(post, user) -> new MaterializedUsersAndPosts(user, post))
			.toStream()
			.foreach((k,v) -> log.info("materializedUsersAndPosts [key {} - data {}]", k, v));
		};
	}
	
	@Bean
	public BiConsumer<KTable<Long, Post>, KTable<Long, Comment>> materializedPostsAndComments() {
		return (posts, comments) -> {
			comments
			.join(posts,
				Comment::getPostId,
				(comment, post) -> new MaterializedPostsAndComments(post, comment))
			.toStream()
			.foreach((k,v) -> log.info("materializedPostsAndComments [key {} - data {}]", k, v));
		};
	}
	
	@RestController
	@RequestMapping("/api")
	public class ApiController {
		
		@PostMapping("/users")
		public ResponseEntity<?> users() {
			User[] users = template.getForObject(config.getApi().getUsers(), User[].class);
			log.info("load {} users.", users == null ? 0 : users.length);
			if(users != null && users.length >0) {
				Arrays
				.asList(users)
				.forEach(e -> 
					bridge.send("uout0", MessageBuilder
							.withPayload(e)
							.setHeader(KafkaHeaders.MESSAGE_KEY,
									e.getId())
							.build()));
				log.info("sended users.");
			}
			return ResponseEntity.ok("");
		}
		
		@PostMapping("/posts")
		public ResponseEntity<?> posts() {
			Post[] posts = template.getForObject(config.getApi().getPosts(), Post[].class);
			log.info("load {} posts.", posts == null ? 0 : posts.length);
			if(posts != null && posts.length > 0) {
				Arrays
				.asList(posts)
				.forEach(e -> 
					bridge.send("pout0", MessageBuilder
							.withPayload(e)
							.setHeader(KafkaHeaders.MESSAGE_KEY,
								e.getId())
							.build()));
				log.info("sended posts.");
			}
			return ResponseEntity.ok("");
		}
		
		@PostMapping("/comments")
		public ResponseEntity<?> comments() {
			Comment[] comments = template.getForObject(config.getApi().getComments(), Comment[].class);
			log.info("load {} comments.", comments == null ? 0 : comments.length);
			if(comments != null && comments.length > 0) {
				Arrays
					.asList(comments)
					.forEach(e -> 
						bridge.send("cout0", MessageBuilder
								.withPayload(e)
								.setHeader(KafkaHeaders.MESSAGE_KEY,
									e.getId())
								.build()));
				log.info("sended comments.");
			}
			return ResponseEntity.ok("");
		}
		
		@PostMapping("/runner")
		public ResponseEntity<?> runner() {
			users();
			posts();
			comments();
			return ResponseEntity.ok("");
		}
	}
}
