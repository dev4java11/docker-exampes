package com.pe.dev4java11;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SpringBootApplication
public class Ms2Application {

	public static void main(String[] args) {
		SpringApplication.run(Ms2Application.class, args);
	}
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public class User {
		
		private String username;
		private String name;
		private Date birthday;
		private String environment;
	}
	
	@Data
	@Configuration
	@ConfigurationProperties(prefix = "config")
	public class Config {
		
		private String phrase;
		private String environment;
	}

	
	@RestController
	@RequestMapping("/api")
	public class ApiController {

		@Autowired
		private Config config;
		
		@GetMapping("/hello")
		public String hello() {
			return "Hello World from " + config.getEnvironment() + ".";
		}
		
		@GetMapping("/phrase")
		public String phrase() {
			return config.getPhrase();
		}
		
		@GetMapping("/user")
		public User user() {
			return new User("admin", "Arthur Cooke", Date.from(LocalDate.of(1991, 3, 14).atStartOfDay(ZoneId.systemDefault()).toInstant()), config.getEnvironment());
		}
	}
}
