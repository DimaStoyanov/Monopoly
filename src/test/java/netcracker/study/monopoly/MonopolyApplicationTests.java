package netcracker.study.monopoly;

import netcracker.study.monopoly.db.model.GameStatistic;
import netcracker.study.monopoly.db.model.Player;
import netcracker.study.monopoly.db.model.Score;
import netcracker.study.monopoly.db.repository.GameRepository;
import netcracker.study.monopoly.db.repository.PlayerRepository;
import netcracker.study.monopoly.db.repository.ScoreRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MonopolyApplicationTests {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private ScoreRepository scoreRepository;


    @Test
    @Transactional
    public void insertAndReadDB() {
        String nickname = "sdjv4j32wsdt43904235";
        Player player = new Player(nickname, new Date());
        GameStatistic game = new GameStatistic(10, new Date(), player);
        Score score = new Score(game, player, 100);

        playerRepository.save(player);
        gameRepository.save(game);
        scoreRepository.save(score);

        Player playerFromDB = playerRepository.findByNickname(nickname);
        Assert.assertTrue(playerFromDB.getStat().getTotalScore() == 100);
        Assert.assertTrue(playerFromDB.getStat().getTotalGames() == 1);
        Assert.assertTrue(playerFromDB.getStat().getTotalWins() == 1);
        Assert.assertEquals(player, playerFromDB);
        Assert.assertEquals(game, gameRepository.findById(game.getId()).orElse(null));
        Assert.assertEquals(score, scoreRepository.findById(score.getId()).orElse(null));
    }
}
