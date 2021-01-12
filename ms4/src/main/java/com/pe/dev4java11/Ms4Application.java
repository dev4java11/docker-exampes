package com.pe.dev4java11;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class Ms4Application {
	
	
	public static final List<String> prhases = List.of("hello world",
			"good luck",
			"geronimo",
			"yes sir",
			"give me a second",
			"nice to meet you", "she is beauty",
			"the little dog", "i'm boring", "yeahhhh");

	public static void main(String[] args) {
		SpringApplication.run(Ms4Application.class, args);
	}

	@Bean
	public Supplier<String> sourcePhrase() {
		Random rnd = new Random();
		return () -> prhases.get(rnd.nextInt(prhases.size()));
	}
	
	@Bean
	public Function<String, String> uppercase(){
		return (x) -> x.toUpperCase();
	}
	
	@Bean
	public Consumer<String> sinkPhrase(){
		return (x) -> log.info("prhase uppercase {}", x);
	}
}
