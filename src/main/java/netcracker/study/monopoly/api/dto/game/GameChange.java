package netcracker.study.monopoly.api.dto.game;

import lombok.Data;
import netcracker.study.monopoly.api.dto.game.cells.Street;

import java.util.UUID;

@Data
public class GameChange {
    private UUID turnOf;
    private Gamer gamerChange;
    private Street streetChange;
}
