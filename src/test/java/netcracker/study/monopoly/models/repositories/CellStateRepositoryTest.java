package netcracker.study.monopoly.models.repositories;

import netcracker.study.monopoly.api.dto.game.GameDto;
import netcracker.study.monopoly.managers.GameManager;
import netcracker.study.monopoly.models.entities.CellState;
import netcracker.study.monopoly.models.entities.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CellStateRepositoryTest {

    @Autowired
    private CellStateRepository cellStateRepository;

    @Autowired
    private GameManager gameManager;

    @Autowired
    private PlayerRepository playerRepository;

    private GameDto gameDto;

    @Before
    public void setUp() {

        Player player = new Player("123");
        playerRepository.save(player);

        gameDto = gameManager.create(Collections.singletonList(player.getId()));
    }

    @Test
    public void findByGameIdAndPosition() {
        Optional<CellState> byGameIdAndPosition = cellStateRepository.findByGameIdAndPosition(gameDto.getId(), 0);
        assertTrue(byGameIdAndPosition.isPresent());
        System.out.println(byGameIdAndPosition);
    }
}