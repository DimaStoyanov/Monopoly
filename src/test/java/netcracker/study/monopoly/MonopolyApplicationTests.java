package netcracker.study.monopoly;

import netcracker.study.monopoly.db.model.Game;
import netcracker.study.monopoly.db.model.Player;
import netcracker.study.monopoly.db.model.Score;
import netcracker.study.monopoly.db.repository.GameRepository;
import netcracker.study.monopoly.db.repository.PlayerRepository;
import netcracker.study.monopoly.db.repository.ScoreRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MonopolyApplicationTests implements ApplicationContextAware {

    private GameRepository gameRepository;
    private PlayerRepository playerRepository;
    private ScoreRepository scoreRepository;

    @Test
    public void contextLoads() {
    }

    @Test
    @Transactional
    public void insertAndReadDB() {
        String nickname = "sdjv4j32wsdt43904235";
        Player player = new Player(nickname, new Date());
        Game game = new Game(10, new Date(), player);
        Score score = new Score(game, player, 100);

        playerRepository.save(player);
        gameRepository.save(game);
        scoreRepository.save(score);

        Player playerFromDB = playerRepository.findByNickname(nickname);
        Assert.assertTrue(playerFromDB.getStat().getTotalScore() == 100);
        Assert.assertTrue(playerFromDB.getStat().getTotalGames() == 1);
        Assert.assertTrue(playerFromDB.getStat().getTotalWins() == 1);
        Assert.assertEquals(player, playerFromDB);
    }




    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        gameRepository = applicationContext.getBean(GameRepository.class);
        playerRepository = applicationContext.getBean(PlayerRepository.class);
        scoreRepository = applicationContext.getBean(ScoreRepository.class);
    }
}
