package netcracker.study.monopoly.controller.dto.cells;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import netcracker.study.monopoly.controller.dto.Gamer;

@ToString
@EqualsAndHashCode(callSuper = true)
public class Street extends Cell {
    @Getter
    @Setter
    private Gamer owner;
}