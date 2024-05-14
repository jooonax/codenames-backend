package at.kaindorf.codenames.controller;

import at.kaindorf.codenames.io.WordReader;
import at.kaindorf.codenames.pojos.*;
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

import java.util.*;
import java.util.List;

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

    private static final Map<String, GameState> gameStateMap = new HashMap<>();
    private static final Map<String, List<Player>> playersMap = new HashMap<>();
    private static final List<String> words = WordReader.readWords();
    private static final Random RANDOM = new Random();

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

    @MessageMapping("/role")
    public Player changeRole(@Payload Player player) {

        List<Player> playersList = playersMap.get(player.getRoomCode()); // get all players from the room of the player object

        if (playersList != null) {
            for (Player p : playersList) {
                if (p.getUsername().equals(player.getUsername())) {

                    boolean spymaster = false;

                    for (Player p1 : playersList) { // check if there's already a spymaster on the team
                        if (p1.getTeam().equals(player.getTeam()) && p1.getRole().equals(Role.MASTER)) {
                            spymaster = true;
                            break;
                        }
                    }

                    if (!spymaster && player.getRole() != Role.NONE) { // if there is no other spymaster, and the role isn't NONE, it passes
                        p.setRole(player.getRole());
                    }
                    if (player.getTeam() != Team.NONE) {
                        p.setTeam(player.getTeam());
                    }
                    break;
                }
            }
        }

        return player;
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
            GameState gameState = new GameState(new ArrayList<>(), new Player(playerJoin.getRoomCode()));
            for (int i = 0; i < 25; i++) {
                int rnd = RANDOM.nextInt(words.size());
                CardColor color = CardColor.WHITE;
                if (i < 9) color = CardColor.BLUE;
                if (i >= 9 && i < 8+9) color = CardColor.RED;
                if (i == 24) color = CardColor.BLACK;
                gameState.getCards().add(new Card(words.get(rnd), color));
            }
            gameStateMap.put(gameState.getSender().getRoomCode(), gameState);
            playersMap.put(playerJoin.getRoomCode(), new ArrayList<>(List.of(playerJoin)));
        }
        return playerJoin;
    }
    @GetMapping("/{roomCode}/players")
    public ResponseEntity<List<Player>> getPlayers(@PathVariable String roomCode) {
        return ResponseEntity.ok(playersMap.get(roomCode));
    }
    @GetMapping("/{roomCode}/gameState")
    public ResponseEntity<GameState> getGameState(@PathVariable String roomCode) {
        return ResponseEntity.ok(gameStateMap.get(roomCode));
    }
}
