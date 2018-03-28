package netcracker.study.monopoly.controller.dto.cells;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
public abstract class Cell {

    @NonNull
    String name;

    @NonNull
    Integer position;
}
