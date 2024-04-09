package at.kaindorf.codenames.beans;

import lombok.AllArgsConstructor;
import org.springframework.web.socket.WebSocketMessage;

/**
 * @author : Jonas
 * @created : 09/04/2024, Tuesday
 **/
@AllArgsConstructor
public class Message implements WebSocketMessage<String> {
    private String text;

    @Override
    public String getPayload() {
        return text;
    }

    @Override
    public int getPayloadLength() {
        return text.length();
    }

    @Override
    public boolean isLast() {
        return false;
    }
}
