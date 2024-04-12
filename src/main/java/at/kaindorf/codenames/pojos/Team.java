package at.kaindorf.codenames.pojos;

import lombok.Data;

import java.util.List;

/**
 * Project: codenames-backend
 * Created by: kocjod20
 * Date: 2024-04-12
 * Time: 16:58:46
 */
@Data
public class Team {
    private TeamColor teamColor;
    private List<String> spymasters;
    private List<String> operatives;
}
