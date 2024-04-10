package at.kaindorf.codenames.beans;

import lombok.Data;

/**
 * Project: codenames
 * Created by: kocjod20
 * Date: 2024-04-10
 * Time: 10:10:21
 */
@Data
public class ChatMessage {
    private MessageType type;
    private String content;
    private String sender;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}
