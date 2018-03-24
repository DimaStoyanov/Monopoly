package netcracker.study.monopoly.game;

import netcracker.study.monopoly.game.cells.Street;
import netcracker.study.monopoly.game.field.Field;
import netcracker.study.monopoly.util.GameChange;
import netcracker.study.monopoly.util.JSONGameRead;

import java.util.List;

public class PlayGame {

    JSONGameRead jsonGameRead;
    List<Gamer> gamers;
    Field field;
    GameChange gameChange;

    public PlayGame(int gamersCount) {
        jsonGameRead = new JSONGameRead(gamersCount);
        gamers = jsonGameRead.getGamers();
        field = new Field();
        field.setCells();
        gameChange = new GameChange();
    }

    public static void main(String[] args) {
        PlayGame game = new PlayGame(100);
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

    public void go(int gamerNum) {
        gameChange.clear();
        gamers.get(gamerNum).go();
        gameChange.addGamerChange(gamers.get(gamerNum));
        System.out.println(field.getCells().get(gamers.get(gamerNum).getPosition()).show());
        gameChange.addStreetChange((Street) field.getCells().get(gamers.get(gamerNum).getPosition()));
    }

    public void action(int gamerNum) {
        System.out.println(field.getCells().get(gamers.get(gamerNum).getPosition()).action(gamers.get(gamerNum)));
        gameChange.addStreetChange((Street) field.getCells().get(gamers.get(gamerNum).getPosition()));
    }

    public GameChange getGameChange() {
        return gameChange;
    }
}
