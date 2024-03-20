package at.kaindorf.codenames.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Jonas
 * @created : 20/03/2024, Wednesday
 **/
@Data@NoArgsConstructor@AllArgsConstructor
public class User {
    public String username;
    public String team;
}
