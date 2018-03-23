package netcracker.study.monopoly.game.cells;

import netcracker.study.monopoly.game.Gamer;

public interface Cell {
    String action(Gamer gamer);

    int getPosition();

    String getName();

    String show();
}
