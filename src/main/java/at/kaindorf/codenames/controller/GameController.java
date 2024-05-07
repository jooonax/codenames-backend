package at.kaindorf.codenames.controller;

import at.kaindorf.codenames.pojos.GameState;
import at.kaindorf.codenames.pojos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: codenames-backend
 * Created by: kocjod20
 * Date: 2024-04-12
 * Time: 17:01:01
 */

@Controller
public class GameController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public static List<User> users = new ArrayList<>();

    @MessageMapping("/game")
    public GameState recMessage(@Payload GameState gameState){
        List<User> usersInRoom = users.stream().filter(u -> u.getRoomCode().equals(gameState.getRoomCode()) && !(u.getUser().equals(gameState.getSender()))).toList();

        for (User receiverName: usersInRoom) {
            simpMessagingTemplate.convertAndSendToUser(receiverName.getUser(),"/state", gameState);
        }

        return gameState;
    }

    @MessageMapping("/join")
    public User join(@Payload User userJoin) {
        users.add(userJoin);
        return userJoin;
    }

}
