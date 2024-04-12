package at.kaindorf.codenames.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
}
