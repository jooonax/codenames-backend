package at.kaindorf.codenames.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

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
    @NonNull
    private List<Card> cards;
    @NonNull
    private Player sender;
    @NonNull
    private Team turn;
    @NonNull
    private boolean started;
    private Clue clue;
    private int flippedCount;
    private Team winner;
}
