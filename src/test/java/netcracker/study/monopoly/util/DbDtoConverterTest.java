package netcracker.study.monopoly.util;

import netcracker.study.monopoly.controller.dto.Gamer;
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

import java.util.Random;
import java.util.UUID;

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


}