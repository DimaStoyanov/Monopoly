package netcracker.study.monopoly.controller.dto.cells;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;


@Getter
@Setter
public class Start extends Cell {

    @NonNull
    Integer salary;

    public Start() {
        name = "Старт";
        position = 0;
        salary = 2000;
    }

}