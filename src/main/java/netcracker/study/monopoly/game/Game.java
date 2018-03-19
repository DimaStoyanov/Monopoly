package netcracker.study.monopoly.game;

import java.util.ArrayList;
import java.util.List;

public class Game {

    List<Gamer> gamers = new ArrayList<>();

    public void start(int gamersCount) {
        for (int j = 0; j < gamersCount; j++) {
            gamers.add(new Gamer());
            gamers.get(j).setPosition(0);
        }

    }
}
