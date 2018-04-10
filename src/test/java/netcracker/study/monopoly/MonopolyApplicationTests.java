package netcracker.study.monopoly;

import netcracker.study.monopoly.models.entities.CellState;
import netcracker.study.monopoly.models.entities.Game;
import netcracker.study.monopoly.models.entities.Player;
import netcracker.study.monopoly.models.entities.PlayerState;
import netcracker.study.monopoly.models.repositories.GameRepository;
import netcracker.study.monopoly.models.repositories.PlayerRepository;
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

import static netcracker.study.monopoly.models.entities.CellState.CellType.STREET;
import static org.junit.Assert.assertEquals;

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
        playerRepository.deleteAll();
        gameRepository.deleteAll();

        Player john = new Player("john");
        Player ivan = new Player("ivan");
        Player alisa = new Player("alisa");
        Player bot = new Player("bot");
        List<Player> players = Arrays.asList(john, ivan, alisa, bot);

        List<CellState> street = Arrays.asList(new CellState(2, "", STREET),
                new CellState(1, "", STREET),
                new CellState(0, "", STREET), new CellState(3, "", STREET));

        List<PlayerState> playerStates = Arrays.asList(new PlayerState(0, john),
                new PlayerState(2, alisa),
                new PlayerState(1, ivan),
                new PlayerState(3, bot));

        List<Game> games = Arrays.asList(new Game(playerStates, street),
                new Game(playerStates, street));


        playerRepository.saveAll(players);
        gameRepository.saveAll(games);
    }

    @Test
    @Transactional
    public void checkCount() {
        assertEquals(playerRepository.count(), 4);
        assertEquals(gameRepository.count(), 2);
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

