package netcracker.study.monopoly.util;

import netcracker.study.monopoly.db.model.Game;
import netcracker.study.monopoly.db.model.Player;
import netcracker.study.monopoly.db.model.PlayerState;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
public class GameCreatorHelperTest {

    private Game game;
    private List<Player> players;


    @Before
    public void setUp() {
        players = new ArrayList<>();
        players.add(new Player("alex"));
        players.add(new Player("egor"));
        players.add(new Player("liza"));
        players.add(new Player("alisa"));
        game = GameCreatorHelper.createGame(players);
    }

    @Test
    public void checkCreatedGame() {
        Assert.assertTrue(game.getPlayerStates().size() == 4);
        Assert.assertTrue(game.getField().size() == 22);
        int initMoney = game.getPlayerStates().get(0).getMoney();
        Assert.assertTrue(game.getPlayerStates().stream().allMatch(p -> p.getMoney() == initMoney));
        Assert.assertEquals(game.getPlayerStates().stream()
                .map(PlayerState::getPlayer)
                .collect(Collectors.toList()), players);
    }
}