package netcracker.study.monopoly.api.controllers.rest;

import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j2;
import netcracker.study.monopoly.api.dto.game.GameChange;
import netcracker.study.monopoly.api.dto.game.GameDto;
import netcracker.study.monopoly.managers.GameManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.UUID;

import static netcracker.study.monopoly.api.controllers.rest.PlayerController.GAME_ID_KEY;
import static netcracker.study.monopoly.api.controllers.rest.PlayerController.PROFILE_ID_KEY;

@RestController
@RequestMapping("/api/v1")
@Api
@Log4j2
public class GameController {

    private final GameManager gameManager;
    private final static String TOPIC_PREFIX = "/topic/games/";

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public GameController(GameManager gameManager, SimpMessagingTemplate messagingTemplate) {
        this.gameManager = gameManager;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/game")
    public GameDto getGame(HttpSession session) {
        UUID gameId = (UUID) session.getAttribute(GAME_ID_KEY);
        log.info("Request get game with id " + gameId);
        return gameManager.getGame(gameId);
    }

    @PutMapping("/street")
    public void buyStreet(HttpSession session,
                          @RequestParam(name = "from") UUID playerId) {
        UUID gameId = (UUID) session.getAttribute(GAME_ID_KEY);
        UUID profileId = (UUID) session.getAttribute(PROFILE_ID_KEY);
        GameChange gameChange = gameManager.streetStep(gameId, playerId, profileId);
        messagingTemplate.convertAndSend(TOPIC_PREFIX + gameId, gameChange);
    }


    @PostMapping("/finish")
    public void finish(@PathVariable UUID gameID, @PathVariable UUID gamerID) {
        log.info("Request finish game with id " + gameID);
        gameManager.finishGame(gameID, gamerID);
    }


}
