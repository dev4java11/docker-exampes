package pe.com.dev4java11.materialized;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.dev4java11.jsonplaceholder.Post;
import pe.com.dev4java11.jsonplaceholder.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterializedUsersAndPosts {

	private User user;
	private Post post;
}
