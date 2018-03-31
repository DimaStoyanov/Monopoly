package netcracker.study.monopoly.util;

import netcracker.study.monopoly.controller.dto.GameDto;
import netcracker.study.monopoly.controller.dto.Gamer;
import netcracker.study.monopoly.controller.dto.cells.Flight;
import netcracker.study.monopoly.controller.dto.cells.Street;
import netcracker.study.monopoly.db.GameCreator;
import netcracker.study.monopoly.db.model.CellState;
import netcracker.study.monopoly.db.model.Game;
import netcracker.study.monopoly.db.model.Player;
import netcracker.study.monopoly.db.model.PlayerState;
import netcracker.study.monopoly.db.repository.PlayerRepository;
import netcracker.study.monopoly.db.repository.PlayerStateRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static netcracker.study.monopoly.db.model.CellState.CellType.FLIGHT;
import static netcracker.study.monopoly.db.model.CellState.CellType.STREET;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DbDtoConverterTest {

    @Autowired
    DbDtoConverter converter;
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

        Gamer actual = converter.playerToDto(ps);
        assertEquals(expected, actual);
    }

    @Test
    @Transactional
    public void gamerToDb() {
        Random random = new Random();
        Player player = new Player("noname");
        pr.save(player);

        int order = 1;
        PlayerState ps = new PlayerState(order, player);
        psr.save(ps);
        UUID id = ps.getId();


        int position = random.nextInt();
        int money = random.nextInt();
        boolean canGo = random.nextBoolean();

        Gamer gamer = new Gamer();
        gamer.setPosition(position);
        gamer.setMoney(money);
        gamer.setCanGo(canGo);
        gamer.setId(new UUID(1, 2));
        gamer.setName("aaa");

        PlayerState actual = converter.gamerToDb(gamer, ps);
        assertTrue(actual.getId() == id);
        assertTrue(ps.getPosition() == position);
        assertTrue(ps.getOrder() == order);
        assertTrue(ps.getCanGo() == canGo);
        assertTrue(ps.getPlayer().equals(player));
        assertTrue(ps.getMoney() == money);
        assertTrue(ps.getGame() == null);
        assertTrue(ps.getScore() == 0);
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

        Street dtoStreet = converter.streetToDto(street);
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
    @Transactional
    public void streetToDb() {
        Integer streetPosition = 0;
        String cellName = "cell";
        CellState streetDb = new CellState(streetPosition, cellName, STREET);
        Random random = new Random();

        Street street = new Street();
        String name = "owner";
        boolean canGo = random.nextBoolean();
        int money = random.nextInt();
        int order = random.nextInt();
        int position = random.nextInt();
        Gamer owner = new Gamer();

        Player player = new Player(name);
        PlayerState ps = new PlayerState(order, player);
        ps.setCanGo(canGo);
        ps.setMoney(money);
        ps.setPosition(position);
        pr.save(player);
        psr.save(ps);

        owner.setName(name);
        owner.setId(new UUID(1, 2));
        owner.setCanGo(canGo);
        owner.setMoney(money);
        owner.setOrder(order);
        owner.setPosition(position);


        Integer cost = 100500;
        street.setName("street");
        street.setPosition(1);
        street.setCost(cost);
        street.setOwner(owner);

        CellState cellState = converter.streetToDb(street, streetDb, ps);

        assertEquals(cellState.getPosition(), streetPosition);
        assertEquals(cellState.getCost(), cost);
        assertEquals(cellState.getName(), cellName);
        assertEquals(cellState.getType(), STREET);
        assertEquals(cellState.getOwner(), ps);

    }

    @Test
    public void flightToDto() {
        Random random = new Random();
        int position = random.nextInt();
        int cost = random.nextInt();
        CellState cell = new CellState(position, "flight", FLIGHT);
        cell.setCost(cost);
        Flight flight = converter.flightToDto(cell);

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
        GameDto game = converter.gameToDto(dbGame);

        assertTrue(game.getField().size() == dbGame.getField().size());
        assertTrue(game.getPlayers().size() == dbGame.getPlayerStates().size());
        assertEquals(game.getTurnOf().getId(), dbGame.getTurnOf().getId());
        dbGame.getField().forEach(c ->
                assertEquals(game.getField().get(c.getPosition()).getName(), c.getName()));
        dbGame.getPlayerStates().forEach(p ->
                assertEquals(p.getId(), dbGame.getPlayerStates().get(p.getOrder()).getId()));
    }

}