package netcracker.study.monopoly.converters;

import netcracker.study.monopoly.api.dto.game.GameDto;
import netcracker.study.monopoly.api.dto.game.Gamer;
import netcracker.study.monopoly.api.dto.game.cells.Cell;
import netcracker.study.monopoly.api.dto.game.cells.Flight;
import netcracker.study.monopoly.api.dto.game.cells.Street;
import netcracker.study.monopoly.models.GameCreator;
import netcracker.study.monopoly.models.entities.CellState;
import netcracker.study.monopoly.models.entities.Game;
import netcracker.study.monopoly.models.entities.Player;
import netcracker.study.monopoly.models.entities.PlayerState;
import netcracker.study.monopoly.models.repositories.GameRepository;
import netcracker.study.monopoly.models.repositories.PlayerRepository;
import netcracker.study.monopoly.models.repositories.PlayerStateRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static netcracker.study.monopoly.models.entities.CellState.CellType.FLIGHT;
import static netcracker.study.monopoly.models.entities.CellState.CellType.STREET;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DbDtoConverterTest {

    @Autowired
    PlayerConverter playerConverter;
    @Autowired
    CellConverter cellConverter;
    @Autowired
    GameConverter gameConverter;
    @Autowired
    PlayerRepository pr;
    @Autowired
    PlayerStateRepository psr;
    @Autowired
    GameRepository gr;

    @Test
    @Transactional
    public void playerToDto() {
        Player player = new Player("dima");
        pr.save(player);

        PlayerState ps = new PlayerState(0, player);
        psr.save(ps);
        Gamer expected = new Gamer();
        expected.setCanGo(ps.getCanGo());
        expected.setId(ps.getId());
        expected.setMoney(ps.getMoney());
        expected.setName(ps.getPlayer().getNickname());
        expected.setOrder(ps.getOrder());
        expected.setPosition(ps.getPosition());

        Gamer actual = playerConverter.toDto(ps);
        assertEquals(expected, actual);
    }


    @Test
    @Transactional
    public void streetToDto() {
        Random random = new Random();
        int cost = random.nextInt();
        int position = random.nextInt() % 23;
        String name = "name";
        Player player = new Player(name);
        PlayerState ps = new PlayerState(0, player);
        pr.save(player);
        psr.save(ps);

        CellState street = new CellState(position, name, STREET);
        street.setCost(cost);
        street.setOwner(ps);

        Street dtoStreet = cellConverter.toStreet(street);
        assertEquals(street.getName(), dtoStreet.getName());
        assertEquals(street.getCost(), dtoStreet.getCost());
        assertEquals(street.getPosition(), dtoStreet.getPosition());
        assertEquals(ps.getPlayer().getNickname(), dtoStreet.getOwner().getName());
        assertEquals(ps.getCanGo(), dtoStreet.getOwner().getCanGo());
        assertEquals(ps.getMoney(), dtoStreet.getOwner().getMoney());
        assertEquals(ps.getPosition(), dtoStreet.getOwner().getPosition());
        assertEquals(ps.getOrder(), dtoStreet.getOwner().getOrder());
        assertEquals(ps.getId(), dtoStreet.getOwner().getId());
        Cell initCell = GameCreator.INSTANCE.getInitCell(position);
        assertEquals(initCell.getImgPath(), dtoStreet.getImgPath());
        assertArrayEquals(initCell.getCellCoordinates(), dtoStreet.getCellCoordinates());
        assertArrayEquals(initCell.getRouteCoordinates(), dtoStreet.getRouteCoordinates());
    }

    @Test
    public void flightToDto() {
        Random random = new Random();
        int position = random.nextInt() % 23;
        int cost = random.nextInt();
        CellState cell = new CellState(position, "flight", FLIGHT);
        cell.setCost(cost);
        Flight flight = cellConverter.toFlight(cell);

        assertEquals(cell.getCost(), flight.getCost());
        assertEquals(cell.getName(), flight.getName());
        assertEquals(cell.getPosition(), flight.getPosition());
        Cell initCell = GameCreator.INSTANCE.getInitCell(position);
        assertEquals(initCell.getImgPath(), flight.getImgPath());
        assertArrayEquals(initCell.getCellCoordinates(), flight.getCellCoordinates());
        assertArrayEquals(initCell.getRouteCoordinates(), flight.getRouteCoordinates());
    }

    public void jailToDto() {

    }

    public void startToDto() {

    }

    @Test
    public void gameToDto() {
        Random random = new Random();
        List<Player> players = Stream.generate(random::nextInt)
                .limit(100)
                .map(n -> new Player("u" + n))
                .collect(Collectors.toList());

        pr.saveAll(players);
        Game dbGame = GameCreator.INSTANCE.createGame(players);
        gr.save(dbGame);
        GameDto game = gameConverter.toDto(dbGame);

        assertEquals(dbGame.getField().size(), game.getField().size());
        assertEquals(dbGame.getPlayerStates().size(), game.getPlayers().size());
        assertEquals(dbGame.getTurnOf().getId(), game.getTurnOf().getId());
        dbGame.getField().forEach(c ->
                assertEquals(c.getName(), game.getField().get(c.getPosition()).getName()));
        dbGame.getPlayerStates().forEach(p ->
                assertEquals(dbGame.getPlayerStates().get(p.getOrder()).getId(), p.getId()));
        long correctCellsCount = game.getField().stream()
                .filter(c -> {
                    Cell initCell = GameCreator.INSTANCE.getInitCell(c.getPosition());
                    return c.getImgPath().equals(initCell.getImgPath())
                            && Arrays.deepEquals(c.getCellCoordinates(), initCell.getCellCoordinates())
                            && Arrays.deepEquals(c.getRouteCoordinates(), initCell.getRouteCoordinates());
                }).count();
        assertEquals(game.getField().size(), correctCellsCount);
    }

}