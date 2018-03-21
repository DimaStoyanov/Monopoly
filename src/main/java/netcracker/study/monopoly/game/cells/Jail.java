package netcracker.study.monopoly.game.cells;

import lombok.Getter;
import lombok.Setter;
import netcracker.study.monopoly.game.Gamer;

public class Jail implements Cell {

    private boolean toImprison = false;

    @Getter
    @Setter
    int money;

    @Getter
    @Setter
    int position;

    @Override
    public void action(Gamer gamer) {
    }


}
