package netcracker.study.monopoly.controller.dto;

import lombok.Data;
import netcracker.study.monopoly.controller.dto.cells.Street;

@Data
public class GameChange {
    private Gamer gamersChange;
    private Street streetChange;
}
