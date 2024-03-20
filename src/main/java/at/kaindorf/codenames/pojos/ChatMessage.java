package at.kaindorf.codenames.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Jonas
 * @created : 15/03/2024, Friday
 **/
@Data@NoArgsConstructor@AllArgsConstructor
public class ChatMessage {
    private String sender;
    private String content;
    private String team;

    public ChatMessage(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }
}