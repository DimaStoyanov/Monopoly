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
    String name;

    @Getter
    @Setter
    int position;

    @Override
    public String action(Gamer gamer) {
        return null;
    }

    @Override
    public String show() {
        return null;
    }

}
