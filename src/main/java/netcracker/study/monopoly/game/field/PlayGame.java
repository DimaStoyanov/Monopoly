package netcracker.study.monopoly.game.field;

import netcracker.study.monopoly.game.Gamer;
import netcracker.study.monopoly.util.JSONRead;

import java.util.List;

public class PlayGame {

    JSONRead jsonRead;
    List<Gamer> gamers;
    Field field;

    public static void main(String[] args) {
        PlayGame game = new PlayGame();
        game.start(100);
        for (int i = 0; i < 100; i++) {
            game.go(i);
            game.action(i);
        }
    }

    public List<Gamer> getGamers() {
        return gamers;
    }

    public void setGamers(List<Gamer> gamers) {
        this.gamers = gamers;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
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
