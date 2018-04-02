package netcracker.study.monopoly.controller.dto;

import lombok.Data;
import netcracker.study.monopoly.controller.dto.cells.Street;

import java.util.UUID;

@Data
public class GameChange {
    private UUID turnOf;
    private Gamer gamerChange;
    private Street streetChange;
}
