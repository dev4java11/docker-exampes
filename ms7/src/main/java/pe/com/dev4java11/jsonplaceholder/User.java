package pe.com.dev4java11.jsonplaceholder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

	private Long id;
	private String name;
	private String username;
	private String email;
	
}
