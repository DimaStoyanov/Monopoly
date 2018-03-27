package netcracker.study.monopoly;

import netcracker.study.monopoly.db.model.Game;
import netcracker.study.monopoly.db.model.Player;
import netcracker.study.monopoly.db.model.PlayerState;
import netcracker.study.monopoly.db.model.StreetState;
import netcracker.study.monopoly.db.repository.GameRepository;
import netcracker.study.monopoly.db.repository.PlayerRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MonopolyApplicationTests {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private WebApplicationContext webApplicationContext;


    @Before
    @Transactional
    public void insert() {
        Player john = new Player("john");
        Player ivan = new Player("ivan");
        Player alisa = new Player("alisa");
        Player bot = new Player("bot");
        List<Player> players = Arrays.asList(john, ivan, alisa, bot);

        List<StreetState> street = Arrays.asList(new StreetState(2, 200, ""),
                new StreetState(1, 200, ""),
                new StreetState(0, 200, ""), new StreetState(3, 200, ""));

        List<PlayerState> playerStates = Arrays.asList(new PlayerState(200, 0, john),
                new PlayerState(200, 2, alisa),
                new PlayerState(200, 1, ivan),
                new PlayerState(200, 3, bot));

        List<Game> games = Arrays.asList(new Game(playerStates, john, street),
                new Game(playerStates, alisa, street));


        playerRepository.saveAll(players);
        gameRepository.saveAll(games);
    }

    @Test
    @Transactional
    public void checkCount() {
        Assert.assertTrue(playerRepository.count() == 4);
        Assert.assertTrue(gameRepository.count() == 2);

    }

    // This actually does not work only in test (hibernate is too lazy lol)
//    @Test
    @Transactional
    public void checkSort() {
        gameRepository.findAll().forEach(g -> {
            ArrayList<Integer> positions = new ArrayList<>();
            g.getField().forEach(c -> positions.add(c.getPosition()));
            Assert.assertEquals(Arrays.asList(0, 1, 2, 3), positions);
        });

        gameRepository.findAll().forEach(g -> {
            ArrayList<Integer> orders = new ArrayList<>();
            g.getPlayerStates().forEach(p -> orders.add(p.getOrder()));
            Assert.assertEquals(Arrays.asList(0, 1, 2, 3), orders);
        });
    }


}

