package netcracker.study.monopoly;

import netcracker.study.monopoly.db.model.Game;
import netcracker.study.monopoly.db.model.Player;
import netcracker.study.monopoly.db.repository.GameRepository;
import netcracker.study.monopoly.db.repository.PlayerRepository;
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

    @Test
    public void contextLoads() {
        System.out.println("hello");
    }


    private Date date = new Date();


    @Test
    @Transactional
    public void insertAndReadDB() {
        Player player = new Player("dima", date);
        playerRepository.save(player);
        Game game = new Game(10, date, player);
        gameRepository.save(game);
        System.out.println(playerRepository.findAll());
        System.out.println(gameRepository.findAll());
        System.out.println(player.getGamesWon());
//        System.out.println(playerRepository.findAll().iterator().next().getGamesWon());
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        gameRepository = applicationContext.getBean(GameRepository.class);
        playerRepository = applicationContext.getBean(PlayerRepository.class);
    }
}
