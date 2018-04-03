package netcracker.study.monopoly.api.dto.game.cells;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import netcracker.study.monopoly.api.dto.game.Gamer;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Street extends Cell {
    @Getter
    @Setter
    private Gamer owner;

}