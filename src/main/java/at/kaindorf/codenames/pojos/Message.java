package at.kaindorf.codenames.pojos;

import lombok.*;

import java.util.List;

/**
 * Project: codenames-backend
 * Created by: kocjod20
 * Date: 2024-04-12
 * Time: 16:27:26
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Message {
    private Player sender;
    private String message;
    private String date;
    private Player target;
}
