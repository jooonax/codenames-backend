package at.kaindorf.codenames.controller;

import at.kaindorf.codenames.pojos.GameState;
import at.kaindorf.codenames.pojos.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

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

    @MessageMapping("/game")
    public GameState recMessage(@Payload GameState gameState){
        for (String receiverName: gameState.getReceiverNames()) {
            simpMessagingTemplate.convertAndSendToUser(receiverName,"/state", gameState);
        }
        return gameState;
    }
}
