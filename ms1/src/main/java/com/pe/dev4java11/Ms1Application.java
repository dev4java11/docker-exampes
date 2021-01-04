package com.pe.dev4java11;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Ms1Application {
	
	public static void main(String[] args) {
		SpringApplication.run(Ms1Application.class, args);
	}
	
	@Bean
	public CommandLineRunner demo(PersonRepository repository) {
		return (args) -> {
			Person p1 = new Person("Carlos", "M");
			Person p2 = new Person("Bob", "M" );
			Person p3 = new Person("Julio", "M");
			Person p4 = new Person("Ricardo", "M");
			Person p5 = new Person("Diego", "M");
			Person p6 = new Person("Mario", "M");
			Person p7 = new Person("Julisa", "F");
			Person p8 = new Person("Ana", "F");
			Person p9 = new Person("Ursula", "F");
			Person p10 = new Person("Arturo", "M");
			Person p11 = new Person("Melisa", "F");
			Person p12 = new Person("Bernardo", "M");
			Person p13 = new Person("Natalia", "F");
			Person p14 = new Person("Caroline", "F");
			Person p15 = new Person("Allison", "F");
			Person p16 = new Person("Mirta", "F");
			List<Person> persons = Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16);
			repository.saveAll(persons);
		};
	}
}
