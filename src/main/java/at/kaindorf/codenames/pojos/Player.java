package at.kaindorf.codenames.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Project: codenames-backend
 * Created by: raph
 * Date: 2024-05-07
 * Time: 14:03:12
 */

@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Player {
    @EqualsAndHashCode.Include
    private int id;
    private String username;
    private String roomCode;

    private Team team;
    private Role role;

    public Player(String roomCode) {
        id = -1;
        username = "bk";
        this.roomCode = roomCode;
        team = Team.NONE;
        role = Role.NONE;
    }
}
