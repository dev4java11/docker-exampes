package pe.com.dev4java11;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee {
	
	private Integer id;
	private String name;
	private char gender;
	private Date birthday;
	private Double salary;
	private String country;
	
}