package at.kaindorf.codenames.controller;

import at.kaindorf.codenames.io.WordReader;
import at.kaindorf.codenames.pojos.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private static int ID = 0;



    @MessageMapping("/game")
    public GameState recGameState(@Payload GameState gameState) {
        if (gameStateMap.containsKey(gameState.getSender().getRoomCode())) {
            gameStateMap.replace(gameState.getSender().getRoomCode(), gameState);
        } else {
            gameStateMap.put(gameState.getSender().getRoomCode(), gameState);
        }

        sendGameState(gameState);

        return gameState;
    }

    private void sendGameState(GameState gameState) {
        List<Player> usersInRoom = playersMap.get(gameState.getSender().getRoomCode());

        for (Player receiver: usersInRoom) {
            if (receiver.getId() != gameState.getSender().getId()) {
                simpMessagingTemplate.convertAndSendToUser(Integer.toString(receiver.getId()), "/state", gameState);
            }
        }
    }
    private void sendPlayer(Player player) {
        List<Player> usersInRoom = playersMap.get(player.getRoomCode());

        for (Player receiver: usersInRoom) {
            if (receiver.getId() != player.getId()) {
                simpMessagingTemplate.convertAndSendToUser(Integer.toString(receiver.getId()), "/player", player);
            }
        }
    }

    @MessageMapping("/message")
    public Message recMessage(@Payload Message message) {
        List<Player> usersInRoom = playersMap.get(message.getSender().getRoomCode());
        List<Player> receivers = new ArrayList<>(usersInRoom);

        Player player = message.getTarget(); // get the player object from the message object

        if (!(player.getUsername().isBlank())) { // if the username is not null, it filters out the user with the specified username
            receivers = receivers.stream().filter(p -> p.getUsername().equals(player.getUsername())).toList();
        }
        if (!(player.getTeam().equals(Team.NONE))) { // if the team is defined, it sends the message only to users of that team
            receivers = receivers.stream().filter(p -> p.getTeam().equals(player.getTeam())).toList();
        }
        if (!(player.getRole().equals(Role.NONE))) { // if the role is defined, it sends the message only to users with that role
            receivers = receivers.stream().filter(p -> p.getRole().equals(player.getRole())).toList();
        }

        receivers.stream()
                .filter(receiver -> receiver.getId() != message.getSender().getId())
                .forEach(receiver -> simpMessagingTemplate.convertAndSendToUser(Integer.toString(receiver.getId()), "/message", message));

        return message;
    }

    private void addReceiversByRoleAndTeam(List<String> parameters, List<Player> receivers, List<Player> usersInRoom, String roleParam, Role role, Team team) {
        if (parameters.contains(roleParam)) {
            receivers.addAll(usersInRoom.stream()
                    .filter(user -> user.getRole().equals(role) && user.getTeam().equals(team))
                    .toList());
        }
    }

    @MessageMapping("/role")
    public Player changeRole(@Payload Player player) {

        List<Player> playersList = playersMap.get(player.getRoomCode()); // get all players from the room of the player object

        if (playersList != null) {
            for (Player p : playersList) {
                if (p.getId() == player.getId()) {

                    boolean spymaster = false;

                    if (player.getRole().equals(Role.MASTER)) {
                        for (Player p1 : playersList) { // check if there's already a spymaster on the team
                            if (p1.getTeam().equals(player.getTeam()) && p1.getRole().equals(Role.MASTER)) {
                                spymaster = true;
                                break;
                            }
                        }
                    }

                    if (!spymaster) {
                        p.setRole(player.getRole());
                        p.setTeam(player.getTeam());
                    }
                    p.setUsername(player.getUsername());
                    sendPlayer(p);
                    break;
                }
            }
        }

        return player;
    }

    @MessageMapping("/join")
    public Player join(@Payload Player playerJoin) {
        if (playersMap.containsKey(playerJoin.getRoomCode())) {

            int id = playerJoin.getId();
            Optional<Player> optionalRejoinPlayer = playersMap.get(playerJoin.getRoomCode())
                    .stream()
                    .filter(p -> p.getId() == id).findFirst();

            if (optionalRejoinPlayer.isPresent()) {
                playerJoin = optionalRejoinPlayer.get();
            } else {
                sendPlayer(playerJoin);
                playersMap.get(playerJoin.getRoomCode()).add(playerJoin);
            }
        } else {
            playersMap.put(playerJoin.getRoomCode(), new ArrayList<>(List.of(playerJoin)));
        }
        return playerJoin;
    }

    @MessageMapping("/start")
    public String startGame(@Payload String roomCode) {
        Team startTeam = RANDOM.nextBoolean() ? Team.BLUE : Team.RED;
        List<String> chosenWords = new ArrayList<>();
        GameState gameState = new GameState(new ArrayList<>(), new Player(roomCode), startTeam, true, null, 0, Team.NONE);
        for (int i = 0; i < 25; i++) {
            String word = "";
            do {
                int rnd = RANDOM.nextInt(words.size());
                word = words.get(rnd);
            } while (chosenWords.contains(word));
            chosenWords.add(word);
            CardColor color = CardColor.WHITE;
            if (i < 9) color = startTeam.equals(Team.BLUE) ? CardColor.BLUE : CardColor.RED;
            if (i >= 9 && i < 8+9) color = startTeam.equals(Team.BLUE) ? CardColor.RED : CardColor.BLUE;
            if (i == 24) color = CardColor.BLACK;
            gameState.getCards().add(new Card(word, color, false, new String[0]));
            Collections.shuffle(gameState.getCards());
        }
        gameStateMap.put(gameState.getSender().getRoomCode(), gameState);
        sendGameState(gameState);
        return roomCode;
    }

    @MessageMapping("/disconnect")
    public Player disconnect(@Payload Player player) {
        playersMap.get(player.getRoomCode()).remove(player);
        sendPlayer(player);
        return player;
    }

    @GetMapping("/{roomCode}/players")
    public ResponseEntity<List<Player>> getPlayers(@PathVariable String roomCode) {
        return ResponseEntity.ok(playersMap.get(roomCode));
    }
    @GetMapping("/{roomCode}/gameState")
    public ResponseEntity<GameState> getGameState(@PathVariable String roomCode) {
        return ResponseEntity.ok(gameStateMap.get(roomCode));
    }
    @GetMapping("/id")
    public ResponseEntity<Integer> getId() {
        return ResponseEntity.ok(ID++);
    }
}
