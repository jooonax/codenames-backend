package at.kaindorf.codenames.controller;

import at.kaindorf.codenames.pojos.GameState;
import at.kaindorf.codenames.pojos.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
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

    public static List<Player> players = new ArrayList<>();

    @MessageMapping("/game")
    public GameState recMessage(@Payload GameState gameState){
        List<Player> usersInRoom = players.stream().filter(u -> u.getRoomCode().equals(gameState.getSender().getRoomCode()) && !(u.getUsername().equals(gameState.getSender().getUsername()))).toList();

        for (Player receiverName: usersInRoom) {
            simpMessagingTemplate.convertAndSendToUser(receiverName.getUsername(),"/state", gameState);
        }

        return gameState;
    }

    @MessageMapping("/join")
    public Player join(@Payload Player playerJoin) {
        players.add(playerJoin);
        return playerJoin;
    }

}
