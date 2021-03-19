package pe.com.dev4java11.materialized;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.dev4java11.jsonplaceholder.Comment;
import pe.com.dev4java11.jsonplaceholder.Post;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterializedPostsAndComments {

	private Post post;
	private Comment comment;
}
