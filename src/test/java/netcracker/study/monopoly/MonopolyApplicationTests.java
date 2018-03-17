package netcracker.study.monopoly;

import netcracker.study.monopoly.db.model.GameStatistic;
import netcracker.study.monopoly.db.model.Player;
import netcracker.study.monopoly.db.model.Score;
import netcracker.study.monopoly.db.repository.GameRepository;
import netcracker.study.monopoly.db.repository.PlayerRepository;
import netcracker.study.monopoly.db.repository.ScoreRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.Random;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MonopolyApplicationTests {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;


    @Before
    public void init() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @Transactional
    public void insertAndReadViaRest() throws Exception {
        Random random = new Random();
        String nickname = "u" + random.nextLong();
        int score = 42;
        String url = String.format("/insert?nickname=%s&score=%s", nickname, score);

        mockMvc.perform(get(url))
                .andExpect(status().isCreated())
                .andExpect(content().string("OK"));

        mockMvc.perform(get("/read/" + nickname))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname", is(nickname)))
                .andExpect(jsonPath("$.stat.totalScore", is(score)))
                .andExpect(jsonPath("$.stat.totalGames", is(1)))
                .andExpect(jsonPath("$.stat.totalWins", is(1)));

    }

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

        Player playerFromDB = playerRepository.findByNickname(nickname).orElse(null);
        Assert.assertEquals(player, playerFromDB);
        Assert.assertTrue(playerFromDB.getStat().getTotalScore() == 100);
        Assert.assertTrue(playerFromDB.getStat().getTotalGames() == 1);
        Assert.assertTrue(playerFromDB.getStat().getTotalWins() == 1);
        Assert.assertEquals(game, gameRepository.findById(game.getId()).orElse(null));
        Assert.assertEquals(score, scoreRepository.findById(score.getId()).orElse(null));
    }
}


