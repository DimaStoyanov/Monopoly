package netcracker.study.monopoly.controller.dto.cells;

import lombok.Getter;
import lombok.Setter;

public class Jail extends Cell {

    @Getter
    @Setter
    Integer cost;
    private boolean toImprison = false;


}
