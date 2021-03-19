package pe.com.dev4java11.jsonplaceholder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {

	private Long id;
	private Long postId;
	private String name;
	private String email;
	private String body;
	
}
