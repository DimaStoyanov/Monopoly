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
    public void action(Gamer gamer) {
    }
}
