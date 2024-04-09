package at.kaindorf.codenames.websockets;

import at.kaindorf.codenames.beans.Message;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author : Jonas
 * @created : 09/04/2024, Tuesday
 **/
@Slf4j
public class ChatServer implements WebSocketHandler {
    private static final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    private static void broadcast(String message, Set<WebSocketSession> sessions) {
        sessions.forEach(s -> {
            try {
                s.sendMessage(new Message(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        broadcast("User " + session.getId() + " has joined the chat.", sessions);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        broadcast("User " + session.getId() + ": " + message, sessions);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        sessions.remove(session);
        broadcast("User " + session.getId() + " has left the chat.", sessions);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}