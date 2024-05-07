package at.kaindorf.codenames.pojos;

import lombok.Data;

/**
 * Project: codenames-backend
 * Created by: raph
 * Date: 2024-05-07
 * Time: 14:03:12
 */

@Data
public class Player {
    private String username;
    private String roomCode;

    private Team team;
    private Role role;
}
