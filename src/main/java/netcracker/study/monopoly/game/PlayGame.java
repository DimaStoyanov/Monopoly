package netcracker.study.monopoly.game;

import netcracker.study.monopoly.game.cells.Cell;
import netcracker.study.monopoly.game.cells.Start;
import netcracker.study.monopoly.game.cells.Street;
import netcracker.study.monopoly.util.JSONGameRead;

import java.util.ArrayList;
import java.util.List;

public class PlayGame {

    private JSONGameRead jsonGameRead;
    private List<Gamer> gamers;
    private List<Cell> field;
    private GameChange gameChange;

    public PlayGame(int gamersCount) {
        jsonGameRead = new JSONGameRead(gamersCount);
        gamers = jsonGameRead.getGamers();
        field = new ArrayList<>();
        gameChange = new GameChange();
    }

    public PlayGame(List<Gamer> gamers, List<Cell> field) {
        this.gamers = gamers;
        this.field = field;
        this.field.add(0, new Start());
        gameChange = new GameChange();
    }

    public static void main(String[] args) {
        PlayGame game = new PlayGame(100);
        for (int i = 0; i < 100; i++) {
            game.go(i);
            game.action(i);
        }
    }


    public void go(int gamerNum) {
        gameChange.clear();
        gamers.get(gamerNum).go();
        gameChange.addGamerChange(gamers.get(gamerNum));
        System.out.println(field.get(gamers.get(gamerNum).getPosition()).show());
        gameChange.addStreetChange((Street) field.get(gamers.get(gamerNum).getPosition()));
    }

    public void action(int gamerNum) {
        System.out.println(field.get(gamers.get(gamerNum).getPosition()).action(gamers.get(gamerNum)));
        gameChange.addStreetChange((Street) field.get(gamers.get(gamerNum).getPosition()));
    }

    public GameChange getGameChange() {
        return gameChange;
    }
}
