package netcracker.study.monopoly.controller;

import netcracker.study.monopoly.db.model.Game;
import netcracker.study.monopoly.db.model.Player;
import netcracker.study.monopoly.db.repository.GameRepository;
import netcracker.study.monopoly.db.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
public class HelloController {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;

    @Autowired
    public HelloController(PlayerRepository playerRepository, GameRepository gameRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
    }

    @RequestMapping(value = "/insert")
    @ResponseBody
    @Transactional
    public String insert(@RequestParam(name = "nickname", required = false, defaultValue = "Anonymous") String nickname,
                         @RequestParam(name = "duration", required = false, defaultValue = "10") Integer duration) {
        Player player = new Player(nickname, new Date());
        if (playerRepository.findByNickname(nickname) != null) {
            return "Not unique nickname";
        }
        playerRepository.save(player);
        gameRepository.save(new Game(duration, new Date(), player));
        return "OK";
    }

    @RequestMapping(value = "/cleanup")
    @ResponseBody
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String clean() {
        gameRepository.deleteAll();
        playerRepository.deleteAll();
        return "OK";
    }

    @RequestMapping(value = "/read")
    @ResponseBody
    public String read() {
        return "Players: <br>" +
                playerRepository.findAll().toString() +
                "<br> Games: <br>" + gameRepository.findAll().toString();
    }

    @RequestMapping(value = "/update")
    @ResponseBody
    @Transactional
    public String updateScore(@RequestParam(name = "nickname") String nickname,
                              @RequestParam(name = "score") Integer score) {
        Player player = playerRepository.findByNickname(nickname);
        if (player == null)
            return "Player not found";
        player.setTotalScore(score);
        playerRepository.save(player);
        return "OK";
    }
}
