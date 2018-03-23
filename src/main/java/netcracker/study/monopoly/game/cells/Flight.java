package netcracker.study.monopoly.game.cells;

import lombok.Getter;
import lombok.Setter;
import netcracker.study.monopoly.game.Gamer;

@Getter
@Setter
public class Flight implements Cell {


    int money;
    int position;

    String name;

    @Override
    public String action(Gamer gamer) {
        return null;
    }

    @Override
    public String show() {
        return null;
    }
}
