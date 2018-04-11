package netcracker.study.monopoly.converters;

import netcracker.study.monopoly.models.GameCreator;
import netcracker.study.monopoly.models.entities.Game;
import netcracker.study.monopoly.models.entities.Player;
import netcracker.study.monopoly.models.entities.PlayerState;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
public class GameCreatorTest {

    private Game game;
    private List<Player> players;

    public GameCreatorTest() {
        setUp();
    }

    public void setUp() {
        players = new ArrayList<>();
        players.add(new Player("alex"));
        players.add(new Player("egor"));
        players.add(new Player("liza"));
        players.add(new Player("alisa"));
        game = GameCreator.INSTANCE.createGame(players);
    }


    @Test
    public void checkCreatedGame() {
        Assert.assertTrue(game.getPlayerStates().size() == 4);
        int fieldSize = game.getField().size();
        Assert.assertTrue(fieldSize == 23);
        int initMoney = game.getPlayerStates().get(0).getMoney();
        Assert.assertTrue(game.getPlayerStates().stream().allMatch(p -> p.getMoney() == initMoney));
        assertEquals(game.getPlayerStates().stream()
                .map(PlayerState::getPlayer)
                .collect(Collectors.toList()), players);

    }

    @Test
    public void checkGetInitCell() {
        int fieldSize = game.getField().size();
        long countOfCorrectCells = Stream.iterate(0, i -> i + 1)
                .limit(fieldSize)
                .map(GameCreator.INSTANCE::getInitCell)
                .filter(c -> Objects.nonNull(c.getCellCoordinates()))
                .filter(c -> Objects.nonNull(c.getRouteCoordinates()))
                .filter(c -> Objects.nonNull(c.getImgPath()))
                .count();
        assertEquals(fieldSize, countOfCorrectCells);
    }
}