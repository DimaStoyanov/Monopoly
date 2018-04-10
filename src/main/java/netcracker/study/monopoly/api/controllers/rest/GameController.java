package netcracker.study.monopoly.api.controllers.rest;

import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j2;
import netcracker.study.monopoly.api.dto.game.GameDto;
import netcracker.study.monopoly.managers.GameManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Api
@Log4j2
public class GameController {

    private final GameManager gameManager;

    @Autowired
    public GameController(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @PostMapping("/create")
    public GameDto createGame(@RequestBody List<UUID> playersId) {
        log.info("Request create game with players id: " + playersId);
        return gameManager.create(playersId);
    }

    @PostMapping("/get-game")
    public GameDto getGame(@RequestBody UUID gameId) {
        log.info("Request get game with id " + gameId);
        return gameManager.getGame(gameId);
    }

    @PostMapping("/game/{gameID}/gamer/{gamerID}/firstStep")
    public void firstStep(@RequestBody UUID gameID, @RequestBody UUID gamerID) {
        log.info("Request gamer first step with id " + gamerID + " in the game this id " + gameID);
        gameManager.firstStep(gameID, gamerID);
    }

    @PostMapping("/game/{gameID}/gamer/{gamerID}/firstStreet")
    public void streetStep(@RequestBody UUID gameID, @RequestBody UUID gamerID) {
        log.info("Request gamer buy street with id " + gamerID + " in the game this id " + gameID);
        gameManager.streetStep(gameID, gamerID);
    }

    @PostMapping("/game/{gameID}/gamer/{gamerID}/flightStep/{position}")
    public void flightStep(@RequestBody UUID gameID, @RequestBody UUID gamerID, @RequestBody Integer position) {
        log.info("Request gamer flight id " + gamerID + " in the game this id " + gameID);
        gameManager.flightStep(gameID, gamerID, position);
    }

    @PostMapping("/finish")
    public void finish(@RequestBody UUID gameID, @RequestBody UUID gamerID) {
        log.info("Request finish game with id " + gameID);
        gameManager.finishGame(gameID, gamerID);
    }



}
