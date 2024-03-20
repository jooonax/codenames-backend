package at.kaindorf.codenames.controllers;

import at.kaindorf.codenames.pojos.ChatMessage;
import at.kaindorf.codenames.pojos.User;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author : Jonas
 * @created : 15/03/2024, Friday
 **/
@Controller
public class ChatController {
    private static final String IP_SESSION_ATTR = "IP_SESSION";

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final Map<String, Set<User>> playersInRooms = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> sessions = new ConcurrentHashMap<>();

    public ChatController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/join")
    @SendTo("/topic/room")
    public ChatMessage joinRoom(ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        String username = message.getSender();
        String roomName = message.getContent();
        String team = message.getTeam(); // assuming the message also contains the team

        // Add the player to the room and team
        playersInRooms.computeIfAbsent(roomName, k -> new HashSet<>()).add(new User(username, team));

        // Broadcast the list of players in the room
        Set<User> players = playersInRooms.get(roomName);
        simpMessagingTemplate.convertAndSend("/topic/room", new ChatMessage("System", "Players in room " + roomName + ": " +
                String.join(", ", players.stream().map(u -> u.getUsername()).toList())));
        return new ChatMessage("System", "You joined room " + roomName + " and chose team " + team);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String username = event.getUser().getName();
        Set<String> rooms = playersInRooms.keySet().stream()
                .filter(room -> playersInRooms.get(room).contains(username))
                .collect(Collectors.toSet());

        for (String room : rooms) {
            playersInRooms.get(room).remove(username);
            Set<User> players = playersInRooms.get(room);
            simpMessagingTemplate.convertAndSend("/topic/room", new ChatMessage("System", "Players in room " + room + ": " +
                    String.join(", ", players.stream().map(User::getUsername).toList())));
        }
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionSubscribeEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String sessionId = headers.getSessionId();
        String roomName = getRoomName(headers);
        sessions.computeIfAbsent(roomName, k -> Collections.synchronizedSet(new HashSet<>()))
                .add(sessionId);
    }

    private String getRoomName(SimpMessageHeaderAccessor headers) {
        return headers.getNativeHeader("roomName").get(0);
    }

    @MessageMapping("/chat")
    @SendTo("/topic/room")
    public ChatMessage sendMessage(ChatMessage message) {
        String username = message.getSender();
        String team = message.getTeam() == null ? "NoTeam" : message.getTeam();
        return new ChatMessage(username, "[" + team + "] " + message.getContent());
    }
}