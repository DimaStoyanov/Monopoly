package netcracker.study.monopoly.api.controllers.rest;

import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j2;
import netcracker.study.monopoly.api.dto.game.GameDto;
import netcracker.study.monopoly.exceptions.CellNotFoundException;
import netcracker.study.monopoly.exceptions.GameNotFoundException;
import netcracker.study.monopoly.exceptions.PlayerNotFoundException;
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
    public GameDto createGame(@RequestBody List<UUID> playersId) throws PlayerNotFoundException {
        log.info("Request create game with players id: " + playersId);
        try {
            return gameManager.create(playersId);
        } catch (CellNotFoundException e) {
            throw new PlayerNotFoundException();
        }
    }

    @PostMapping("/get-game")
    public GameDto getGame(@RequestBody UUID gameId) throws GameNotFoundException {
        log.info("Request get game with id " + gameId);
        try {
            return gameManager.getGame(gameId);
        } catch (CellNotFoundException e) {
            log.debug(e);
            throw new GameNotFoundException(gameId);
        }
    }


}
