package netcracker.study.monopoly;

import netcracker.study.monopoly.db.model.CellState;
import netcracker.study.monopoly.db.model.Game;
import netcracker.study.monopoly.db.model.Player;
import netcracker.study.monopoly.db.model.PlayerState;
import netcracker.study.monopoly.db.repository.GameRepository;
import netcracker.study.monopoly.db.repository.PlayerRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MonopolyApplicationTests {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private final Random random = new Random();



    @Test
    public void insert() {
        mockMvc = webAppContextSetup(webApplicationContext).build();

        Player john = new Player("john");
        Player ivan = new Player("ivan");
        Player alisa = new Player("alisa");
        Player bot = new Player("bot");
        List<Player> players = Arrays.asList(john, ivan, alisa, bot);

        List<CellState> cellStates = Arrays.asList(new CellState(0), new CellState(1),
                new CellState(2), new CellState(3));

        List<PlayerState> playerStates = Arrays.asList(new PlayerState(200, 0, john),
                new PlayerState(200, 1, ivan),
                new PlayerState(200, 2, alisa),
                new PlayerState(200, 3, bot));

        List<Game> games = Arrays.asList(new Game(playerStates, john, cellStates),
                new Game(playerStates, alisa, cellStates));


        playerRepository.saveAll(players);
        gameRepository.saveAll(games);


        Assert.assertTrue(playerRepository.count() == 4);
        Assert.assertTrue(gameRepository.count() == 2);
    }


}

