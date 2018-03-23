package netcracker.study.monopoly.game;

import netcracker.study.monopoly.game.field.Field;
import netcracker.study.monopoly.util.JSONRead;

import java.util.List;

public class Game {

    JSONRead jsonRead;
    List<Gamer> gamers;
    Field field;

    public static void main(String[] args) {
        Game game = new Game();
        game.start(100);
        for (int i = 0; i < 100; i++) {
            game.go(i);
            game.action(i);
        }
    }

    public void start(int gamersCount) {
        jsonRead = new JSONRead(gamersCount);
        gamers = jsonRead.getGamers();
        field = new Field();
        field.setCells();
    }

    public void go(int gamerNum) {
        gamers.get(gamerNum).go();
        System.out.println(field.getCells().get(gamers.get(gamerNum).getPosition()).show());
    }

    public void action(int gamerNum) {
        System.out.println(field.getCells().get(gamers.get(gamerNum).getPosition()).action(gamers.get(gamerNum)));
    }
}
