package netcracker.study.monopoly.controller;

import netcracker.study.monopoly.db.model.Game;
import netcracker.study.monopoly.db.model.Player;
import netcracker.study.monopoly.db.model.Score;
import netcracker.study.monopoly.db.repository.GameRepository;
import netcracker.study.monopoly.db.repository.PlayerRepository;
import netcracker.study.monopoly.db.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
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
    @ResponseBody
    public String insert(@RequestParam(name = "nickname", defaultValue = "Anonymous") String nickname,
                         @RequestParam(name = "duration", defaultValue = "10") Integer duration) {
        if (playerRepository.findByNickname(nickname) != null) {
            System.out.println(playerRepository.findByNickname(nickname));
            return "Not unique nickname";
        }
        Player player = new Player(nickname, new Date());
        Game game = new Game(duration, new Date(), player);
        playerRepository.save(player);
        gameRepository.save(game);
        scoreRepository.save(new Score(game, player, 100));
        return "OK";
    }

    @RequestMapping(value = "/cleanup")
    @ResponseBody
    public String clean() {
        scoreRepository.deleteAll();
        gameRepository.deleteAll();
        playerRepository.deleteAll();
        return "OK";
    }

    @RequestMapping(value = "/read")
    @ResponseBody
    public String read() {
        return "Players: <br>" +
                playerRepository.findAll().toString() +
                "<br> Games: <br>" + gameRepository.findAll().toString()
                + "<br> Scores: <br>" + scoreRepository.findAll().toString();
    }

    @RequestMapping(value = "/update")
    @ResponseBody
    public String updateScore(@RequestParam(name = "nickname") String nickname,
                              @RequestParam(name = "score") Integer score) {
        Player player = playerRepository.findByNickname(nickname);
        if (player == null)
            return "Player not found";
        player.getStat().addTotalScore(score);
        playerRepository.save(player);
        return "OK";
    }


    @RequestMapping(value = "/stuff")
    @ResponseBody
    public String stuff() {
        prepossess();

        Player player = playerRepository.findByNickname("john");
        return player + "<br>"
                + player.getGamesWon() + "<br>"
                + player.getScores();

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void prepossess() {
        Date date = new Date();

        Player john = new Player("john", date);
        Player alex = new Player("alex", new Date());
        Player alisa = new Player("alisa", date);
        Player xXxNAGIBATORxXx = new Player("xXxNAGIBATORxXx", date);

        List<Player> players = Arrays.asList(john, alex, alisa, xXxNAGIBATORxXx);

        Game game = new Game(35, new Date(), john);
        Game game2 = new Game(5, date, alisa);

        List<Game> games = Arrays.asList(game, game2);

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
