package at.kaindorf.codenames.controllers;

import at.kaindorf.codenames.pojos.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : Jonas
 * @created : 15/03/2024, Friday
 **/
@Controller
public class ChatController {
    private Map<String, String> userTeams = new HashMap<>();

    @MessageMapping("/join")
    @SendTo("/topic/room")
    public ChatMessage joinRoom(ChatMessage message) {
        String username = message.getSender();
        String team = message.getContent();
        userTeams.put(username, team);
        return new ChatMessage(username, "joined room and chose team " + team);
    }

    @MessageMapping("/chat")
    @SendTo("/topic/room")
    public ChatMessage sendMessage(ChatMessage message) {
        String username = message.getSender();
        String team = userTeams.get(username);
        return new ChatMessage(username, "[" + team + "] " + message.getContent());
    }
}
