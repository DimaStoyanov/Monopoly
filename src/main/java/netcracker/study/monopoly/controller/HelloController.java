package netcracker.study.monopoly.controller;

import io.swagger.annotations.Api;
import netcracker.study.monopoly.controller.dto.GameDto;
import netcracker.study.monopoly.db.GameCreator;
import netcracker.study.monopoly.db.model.CellState;
import netcracker.study.monopoly.db.model.Game;
import netcracker.study.monopoly.db.model.Player;
import netcracker.study.monopoly.db.model.PlayerState;
import netcracker.study.monopoly.db.repository.GameRepository;
import netcracker.study.monopoly.db.repository.PlayerRepository;
import netcracker.study.monopoly.util.DbDtoConverter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static netcracker.study.monopoly.db.model.CellState.CellType.STREET;

@RestController
@Api
public class HelloController {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final DbDtoConverter converter;


    public HelloController(PlayerRepository playerRepository,
                           GameRepository gameRepository, DbDtoConverter converter) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.converter = converter;
    }


    @RequestMapping("/dto")
    public GameDto getDto() {
        Game game = GameCreator.INSTANCE.createGame(Arrays.asList(
                new Player("1"),
                new Player("2")
        ));
        return converter.gameToDto(game);
    }


    @RequestMapping(value = "/insert")
    @ResponseStatus(HttpStatus.CREATED)
    public String insert(@RequestParam(name = "nickname", defaultValue = "Anonymous") String nickname) {
        if (playerRepository.findByNickname(nickname).isPresent()) {
            throw new PlayerAlreadyExistException(nickname);
        }
        Player player = new Player(nickname);
        playerRepository.save(player);


        PlayerState playerState = new PlayerState(0, player);
        List<CellState> streetStates = Collections.singletonList(
                new CellState(3, "noname", STREET));
        List<PlayerState> playerStates = Collections.singletonList(playerState);
        Game game = new Game(playerStates, streetStates);
        gameRepository.save(game);
        return "OK";
    }

    @RequestMapping("/add_game")
    public String addGame(@RequestParam(name = "nickname", defaultValue = "Anonymous") String nickname) {
        Player player = playerRepository.findByNickname(nickname).orElseThrow(() ->
                new PlayerNotFoundException(nickname));
        List<PlayerState> playerStates = Collections.singletonList(new PlayerState(0, player));
        List<CellState> streetStates = Collections.singletonList(new CellState(1, "", STREET));
        Game game = new Game(playerStates, streetStates);
        gameRepository.save(game);
        return "OK";
    }


    @RequestMapping(value = "/read/{nickname}")
    public Player read(@PathVariable String nickname) {
        return playerRepository.findByNickname(nickname).orElseThrow(() ->
                new PlayerNotFoundException(nickname));
    }

    @RequestMapping("/read")
    public Iterable<Player> readAll() {
        return playerRepository.findAll();
    }

    @RequestMapping("/read_games")
    public Iterable<Game> readGames() {
        return gameRepository.findAll();
    }

    @RequestMapping("/read_games/{nickname}")
    public Iterable<Game> readPlayerGames(@PathVariable String nickname) {
        Player player = playerRepository.findByNickname(nickname).orElseThrow(() ->
                new PlayerNotFoundException(nickname));
        return gameRepository.findByPlayer(player);
    }

    @RequestMapping(value = "/update")
    public String updateScore(@RequestParam(name = "nickname") String nickname,
                              @RequestParam(name = "score") Integer score) {
        Player player = playerRepository.findByNickname(nickname).
                orElseThrow(() -> new PlayerNotFoundException(nickname));
        player.getStat().addTotalScore(score);
        playerRepository.save(player);
        return "OK";
    }

    @RequestMapping("/add_friend")
    public String addFriend(@RequestParam(name = "from") String from,
                            @RequestParam(name = "to") String to) {
        if (from.equals(to)) return "You can't be friend of yourself";
        Player playerFrom = playerRepository.findByNickname(from).orElseThrow(() ->
                new PlayerNotFoundException(from));
        Player playerTo = playerRepository.findByNickname(to).orElseThrow(() ->
                new PlayerNotFoundException(to));
        playerFrom.addFriend(playerTo);
        playerRepository.save(playerFrom);
        return "OK";
    }

    @RequestMapping("/read_friends")
    public Iterable<Player> readFriends(@RequestParam(name = "nickname", defaultValue = "Anonymous") String nickname) {
        Player player = playerRepository.findByNickname(nickname).orElseThrow(() ->
                new PlayerNotFoundException(nickname));
        return player.getFriends();
    }

    @RequestMapping("/remove_friend")
    public String removeFriend(@RequestParam(name = "from") String from,
                               @RequestParam(name = "to") String to) {
        Player playerFrom = playerRepository.findByNickname(from).orElseThrow(() ->
                new PlayerNotFoundException(from));
        Player playerTo = playerRepository.findByNickname(to).orElseThrow(() ->
                new PlayerNotFoundException(from));

        boolean deleted = playerFrom.removeFriend(playerTo);
        playerRepository.save(playerFrom);

        return deleted ? "OK" : "Can't find your friend " + to;
    }


}


@ResponseStatus(HttpStatus.NOT_FOUND)
class PlayerNotFoundException extends RuntimeException {
    PlayerNotFoundException(String nickname) {
        super("Could not find player " + nickname);
    }
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
class PlayerAlreadyExistException extends RuntimeException {
    PlayerAlreadyExistException(String nickname) {
        super("Not unique nickname: " + nickname);
    }
}
