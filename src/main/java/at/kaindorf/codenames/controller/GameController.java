package at.kaindorf.codenames.controller;

import at.kaindorf.codenames.pojos.GameState;
import at.kaindorf.codenames.pojos.Message;
import at.kaindorf.codenames.pojos.Player;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project: codenames-backend
 * Created by: kocjod20
 * Date: 2024-04-12
 * Time: 17:01:01
 */

@Controller
@RestController
@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/app")
public class GameController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public static Map<String, GameState> gameStateMap = new HashMap<>();
    public static Map<String, List<Player>> playersMap = new HashMap<>();

    @MessageMapping("/game")
    public GameState recMessage(@Payload GameState gameState){
        if (gameStateMap.containsKey(gameState.getSender().getRoomCode())) {
            gameStateMap.replace(gameState.getSender().getRoomCode(), gameState);
        } else {
            gameStateMap.put(gameState.getSender().getRoomCode(), gameState);
        }

        List<Player> usersInRoom = playersMap.get(gameState.getSender().getRoomCode());

        for (Player receiver: usersInRoom) {
            if (!receiver.getUsername().equals(gameState.getSender().getUsername())) {
                simpMessagingTemplate.convertAndSendToUser(receiver.getUsername(), "/state", gameState);
            }
        }

        return gameState;
    }

    @MessageMapping("/message")
    public Message recMessage(@Payload Message message) {
        List<Player> usersInRoom = playersMap.get(message.getSender().getRoomCode());
        for (Player receiver: usersInRoom) {
            if (!receiver.getUsername().equals(message.getSender().getUsername())) {
                simpMessagingTemplate.convertAndSendToUser(receiver.getUsername(),"/message", message);
            }
        }

        return message;
    }

    @MessageMapping("/join")
    public Player join(@Payload Player playerJoin) {
        if (playersMap.containsKey(playerJoin.getRoomCode())) {

            List<Player> usersInRoom = playersMap.get(playerJoin.getRoomCode());
            for (Player receiver: usersInRoom) {
                if (!receiver.getUsername().equals(playerJoin.getUsername())) {
                    simpMessagingTemplate.convertAndSendToUser(receiver.getUsername(),"/joined", playerJoin);
                }
            }
            playersMap.get(playerJoin.getRoomCode()).add(playerJoin);

        } else {
            playersMap.put(playerJoin.getRoomCode(), new ArrayList<>(List.of(playerJoin)));
        }
        return playerJoin;
    }
    @GetMapping("/{roomCode}/players")
    public ResponseEntity<List<Player>> getPlayers(@PathVariable String roomCode) {
        return ResponseEntity.ok(playersMap.get(roomCode));
    }

}
