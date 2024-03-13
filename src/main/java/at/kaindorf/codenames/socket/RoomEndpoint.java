package at.kaindorf.codenames.socket;


import at.kaindorf.codenames.pojos.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author : Jonas
 * @created : 13/03/2024, Wednesday
 **/

@ServerEndpoint(value = "room/{username}")
public class RoomEndpoint {
    private Session session;
    private static Set<RoomEndpoint> roomEndpointSet
            = new CopyOnWriteArraySet<>();
    private static HashMap<String, String> users = new HashMap<>();
    private static ObjectMapper mapper = new ObjectMapper();
    @OnOpen
    public void onOpen(
            Session session,
            @PathParam("username") String username) throws IOException, EncodeException {

        this.session = session;
        roomEndpointSet.add(this);
        users.put(session.getId(), username);

        Message message = new Message();
        message.setFrom(username);
        message.setContent("Connected!");
        ObjectMapper mapper = new ObjectMapper();
        broadcast(message);
    }

    @OnMessage
    public void onMessage(Session session, Message message)
            throws IOException, EncodeException {

        message.setFrom(users.get(session.getId()));
        broadcast(message);
    }

    @OnClose
    public void onClose(Session session) throws IOException, EncodeException {

        roomEndpointSet.remove(this);
        Message message = new Message();
        message.setFrom(users.get(session.getId()));
        message.setContent("Disconnected!");
        broadcast(message);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }

    private static void broadcast(Message message)
            throws IOException, EncodeException {
        roomEndpointSet.forEach(endpoint -> {
            synchronized (endpoint) {
                try {
                    endpoint.session.getBasicRemote().
                            sendObject(mapper.writeValueAsString(message));
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
