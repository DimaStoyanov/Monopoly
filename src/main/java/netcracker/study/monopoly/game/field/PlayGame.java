package netcracker.study.monopoly.game.field;

import netcracker.study.monopoly.game.Gamer;
import netcracker.study.monopoly.game.cells.Cell;
import netcracker.study.monopoly.util.JSONGameRead;

import java.util.ArrayList;
import java.util.List;

public class PlayGame {

    JSONGameRead jsonGameRead;
    List<Gamer> gamers;
    Field field;
    List<Gamer> gamersUpdate;
    List<Cell> cellsUpdate;

    public PlayGame(int gamersCount) {
        jsonGameRead = new JSONGameRead(gamersCount);
        gamers = jsonGameRead.getGamers();
        field = new Field();
        field.setCells();
        gamersUpdate = new ArrayList<>();
        cellsUpdate = new ArrayList<>();
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
        cellsUpdate.clear();
        gamersUpdate.clear();
        gamers.get(gamerNum).go();
        gamersUpdate.add(gamers.get(gamerNum));
        System.out.println(field.getCells().get(gamers.get(gamerNum).getPosition()).show());
        cellsUpdate.add(field.getCells().get(gamers.get(gamerNum).getPosition()));
    }

    public void action(int gamerNum) {
        System.out.println(field.getCells().get(gamers.get(gamerNum).getPosition()).action(gamers.get(gamerNum)));
        cellsUpdate.add(field.getCells().get(gamers.get(gamerNum).getPosition()));
    }

    public List<Gamer> getGamersUpdate() {
        return gamersUpdate;
    }

    public List<Cell> getCellsUpdate() {
        return cellsUpdate;
    }
}
