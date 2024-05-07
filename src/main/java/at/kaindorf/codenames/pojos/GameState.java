package at.kaindorf.codenames.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Project: codenames-backend
 * Created by: kocjod20
 * Date: 2024-04-12
 * Time: 16:57:38
 */
@Data
@AllArgsConstructor
public class GameState {
    private List<Card> cards;
    private List<Team> teams;
    private String sender;
    private String roomCode;
    private List<Message> gameMessages;
}
