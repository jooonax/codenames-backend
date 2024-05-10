package at.kaindorf.codenames.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Project: codenames-backend
 * Created by: kocjod20
 * Date: 2024-04-12
 * Time: 16:55:54
 */
@Data
@AllArgsConstructor
public class Card {
    private String word;
    private CardColor color;
}
