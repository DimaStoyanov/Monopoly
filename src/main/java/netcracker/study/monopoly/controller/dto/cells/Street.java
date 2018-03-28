package netcracker.study.monopoly.controller.dto.cells;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import netcracker.study.monopoly.controller.dto.Gamer;

@Getter
@ToString
public class Street extends Cell {

    @Setter
    @NonNull
    private Integer cost;
    @Setter
    private Gamer owner;


    public Street(Integer position, String name) {
        this.name = name;
        this.position = position;
    }
}
