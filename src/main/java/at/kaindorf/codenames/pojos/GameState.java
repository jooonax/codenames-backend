package at.kaindorf.codenames.pojos;

import lombok.Data;
import org.apache.catalina.User;

import java.util.List;

/**
 * Project: codenames-backend
 * Created by: kocjod20
 * Date: 2024-04-12
 * Time: 16:57:38
 */
@Data
public class GameState {
    private List<Card> cards;
    private List<Team> teams;
    private List<String> receiverNames;
}
