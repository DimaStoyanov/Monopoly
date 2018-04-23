package netcracker.study.monopoly.api.controllers.rest;

import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j2;
import netcracker.study.monopoly.api.dto.game.GameChange;
import netcracker.study.monopoly.api.dto.game.GameDto;
import netcracker.study.monopoly.managers.GameManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@Api
@Log4j2
public class GameController {

    private final GameManager gameManager;

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public GameController(GameManager gameManager, SimpMessagingTemplate messagingTemplate) {
        this.gameManager = gameManager;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/game/{gameId}")
    public GameDto getGame(@PathVariable("gameId") UUID gameId) {
        log.info("Request get game with id " + gameId);
        return gameManager.getGame(gameId);
    }

    @PostMapping("/game/{gameId}/gamer/{gamerId}/firstStep")
    public void firstStep(@PathVariable("gameId") UUID gameID, @PathVariable("gamerId") UUID gamerID) {
        log.info("Request gamer first step with id " + gamerID + " in the game this id " + gameID);
        GameChange gameChange = gameManager.firstStep(gameID, gamerID);
        messagingTemplate.convertAndSend("/topic/games/" + gameID, gameChange);

    }

    @PostMapping("/game/{gameID}/gamer/{gamerID}/firstStreet")
    public void streetStep(@PathVariable UUID gameID, @PathVariable UUID gamerID) {
        log.info("Request gamer buy street with id " + gamerID + " in the game this id " + gameID);
        gameManager.streetStep(gameID, gamerID);
    }

    @PostMapping("/game/{gameID}/gamer/{gamerID}/flightStep/{position}")
    public void flightStep(@PathVariable UUID gameID, @PathVariable UUID gamerID, @PathVariable Integer position) {
        log.info("Request gamer flight id " + gamerID + " in the game this id " + gameID);
        gameManager.flightStep(gameID, gamerID, position);
    }

    @PostMapping("/finish")
    public void finish(@PathVariable UUID gameID, @PathVariable UUID gamerID) {
        log.info("Request finish game with id " + gameID);
        gameManager.finishGame(gameID, gamerID);
    }



}
