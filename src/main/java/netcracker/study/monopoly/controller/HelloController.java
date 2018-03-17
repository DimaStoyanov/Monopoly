package netcracker.study.monopoly.controller;

import netcracker.study.monopoly.db.model.GameStatistic;
import netcracker.study.monopoly.db.model.Player;
import netcracker.study.monopoly.db.model.Score;
import netcracker.study.monopoly.db.repository.GameRepository;
import netcracker.study.monopoly.db.repository.PlayerRepository;
import netcracker.study.monopoly.db.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
public class HelloController {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final ScoreRepository scoreRepository;

    @Autowired
    public HelloController(PlayerRepository playerRepository, GameRepository gameRepository, ScoreRepository scoreRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.scoreRepository = scoreRepository;
    }

    @RequestMapping(value = "/insert")
    @ResponseStatus(HttpStatus.CREATED)
    public String insert(@RequestParam(name = "nickname", defaultValue = "Anonymous") String nickname,
                         @RequestParam(name = "score", defaultValue = "100") int score,
                         @RequestParam(name = "duration", defaultValue = "10") int duration) {
        if (playerRepository.findByNickname(nickname).isPresent()) {
            throw new BadRequestException(nickname);
        }
        Player player = new Player(nickname, new Date());
        GameStatistic game = new GameStatistic(duration, new Date(), player);
        playerRepository.save(player);
        gameRepository.save(game);
        scoreRepository.save(new Score(game, player, score));
        return "OK";
    }

    @RequestMapping(value = "/cleanup")
    public String clean() {
        scoreRepository.deleteAll();
        gameRepository.deleteAll();
        playerRepository.deleteAll();
        return "OK";
    }

    @RequestMapping(value = "/read/{nickname}")
    public Player read(@PathVariable String nickname) {
        return playerRepository.findByNickname(nickname).orElseThrow(() -> new PlayerNotFoundException(nickname));
    }

    @RequestMapping("/read")
    public Iterable<Player> readAll() {
        return playerRepository.findAll();
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


    @RequestMapping(value = "/stuff")
    public Player stuff() {
        prepossess();
        return playerRepository.findByNickname("john").orElseThrow(() -> new PlayerNotFoundException("john"));

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void prepossess() {
        Date date = new Date();

        Player john = new Player("john", date);
        Player alex = new Player("alex", new Date());
        Player alisa = new Player("alisa", date);
        Player xXxNAGIBATORxXx = new Player("xXxNAGIBATORxXx", date);

        List<Player> players = Arrays.asList(john, alex, alisa, xXxNAGIBATORxXx);

        GameStatistic game = new GameStatistic(35, new Date(), john);
        GameStatistic game2 = new GameStatistic(5, date, alisa);


        List<GameStatistic> games = Arrays.asList(game, game2);

        Score johnScore = new Score(game, john, 100);
        Score alexScore = new Score(game, alex, 78);
        Score alisaScore = new Score(game, alisa, 53);
        Score xXxNAGIBATORxXxScore = new Score(game, xXxNAGIBATORxXx, 99);
        Score johnScore2 = new Score(game2, john, 1);
        Score alexScore2 = new Score(game2, alex, 13);
        Score alisaScore2 = new Score(game2, alisa, 22);
        Score xXxNAGIBATORxXxScore2 = new Score(game2, xXxNAGIBATORxXx, 19);


        List<Score> scores = Arrays.asList(johnScore, alexScore, alisaScore, xXxNAGIBATORxXxScore,
                johnScore2, alexScore2, alisaScore2, xXxNAGIBATORxXxScore2);

        playerRepository.saveAll(players);
        gameRepository.saveAll(games);
        scoreRepository.saveAll(scores);


    }
}


@ResponseStatus(HttpStatus.NOT_FOUND)
class PlayerNotFoundException extends RuntimeException {
    PlayerNotFoundException(String nickname) {
        super("Could not find player " + nickname);
    }
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
class BadRequestException extends RuntimeException {
    BadRequestException(String nickname) {
        super("Not unique nickname: " + nickname);
    }
}
