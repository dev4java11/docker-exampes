package pe.com.dev4java11.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "jsonplaceholder")
public class JsonPlaceholderConfig {

	private Api api = new Api();
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Api {
		
		private String users;
		private String posts;
		private String comments;
	}
}
