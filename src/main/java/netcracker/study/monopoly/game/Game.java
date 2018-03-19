package netcracker.study.monopoly.game;

import java.util.ArrayList;
import java.util.List;

public class Game {

    List<Gamer> gamers = new ArrayList<>();

    public void start(int i){
        for (int j = 0; j < i; j++) {
            gamers.add(new Gamer());
            gamers.get(j).setPosition(0);
        }

    }
}
