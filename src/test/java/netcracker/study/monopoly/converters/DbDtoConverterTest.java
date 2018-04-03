package netcracker.study.monopoly.converters;

import netcracker.study.monopoly.api.dto.game.GameDto;
import netcracker.study.monopoly.api.dto.game.Gamer;
import netcracker.study.monopoly.api.dto.game.cells.Flight;
import netcracker.study.monopoly.api.dto.game.cells.Street;
import netcracker.study.monopoly.models.GameCreator;
import netcracker.study.monopoly.models.entities.CellState;
import netcracker.study.monopoly.models.entities.Game;
import netcracker.study.monopoly.models.entities.Player;
import netcracker.study.monopoly.models.entities.PlayerState;
import netcracker.study.monopoly.models.repositories.PlayerRepository;
import netcracker.study.monopoly.models.repositories.PlayerStateRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static netcracker.study.monopoly.models.entities.CellState.CellType.FLIGHT;
import static netcracker.study.monopoly.models.entities.CellState.CellType.STREET;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        int position = random.nextInt();
        String name = "name";
        Player player = new Player(name);
        PlayerState ps = new PlayerState(0, player);
        pr.save(player);
        psr.save(ps);

        CellState street = new CellState(position, name, STREET);
        street.setCost(cost);
        street.setOwner(ps);

        Street dtoStreet = cellConverter.toStreet(street);
        assertEquals(dtoStreet.getName(), street.getName());
        assertEquals(dtoStreet.getCost(), street.getCost());
        assertEquals(dtoStreet.getPosition(), street.getPosition());
        assertEquals(dtoStreet.getOwner().getName(), ps.getPlayer().getNickname());
        assertEquals(dtoStreet.getOwner().getCanGo(), ps.getCanGo());
        assertEquals(dtoStreet.getOwner().getMoney(), ps.getMoney());
        assertEquals(dtoStreet.getOwner().getPosition(), ps.getPosition());
        assertEquals(dtoStreet.getOwner().getOrder(), ps.getOrder());
        assertEquals(dtoStreet.getOwner().getId(), ps.getId());
    }

    @Test
    public void flightToDto() {
        Random random = new Random();
        int position = random.nextInt();
        int cost = random.nextInt();
        CellState cell = new CellState(position, "flight", FLIGHT);
        cell.setCost(cost);
        Flight flight = cellConverter.toFlight(cell);

        assertEquals(flight.getCost(), cell.getCost());
        assertEquals(flight.getName(), cell.getName());
        assertEquals(flight.getPosition(), cell.getPosition());
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

        Game dbGame = GameCreator.INSTANCE.createGame(players);
        GameDto game = gameConverter.toDto(dbGame);

        assertTrue(game.getField().size() == dbGame.getField().size());
        assertTrue(game.getPlayers().size() == dbGame.getPlayerStates().size());
        assertEquals(game.getTurnOf().getId(), dbGame.getTurnOf().getId());
        dbGame.getField().forEach(c ->
                assertEquals(game.getField().get(c.getPosition()).getName(), c.getName()));
        dbGame.getPlayerStates().forEach(p ->
                assertEquals(p.getId(), dbGame.getPlayerStates().get(p.getOrder()).getId()));
    }

}