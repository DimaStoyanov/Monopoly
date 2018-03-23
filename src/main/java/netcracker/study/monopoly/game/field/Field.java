package netcracker.study.monopoly.game.field;

import netcracker.study.monopoly.game.cells.Cell;
import netcracker.study.monopoly.game.cells.Start;
import netcracker.study.monopoly.game.cells.Street;
import netcracker.study.monopoly.util.JSONGameRead;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Field {
    List<Cell> cells = new ArrayList<>();
    Start start = new Start();

    public static void main(String[] args) {
        Field field = new Field();
        field.setCells();
        for (int i = 0; i < field.getCells().size(); i++) {
            System.out.println(field.getCells().get(i).getName());
        }
    }

    public List<Cell> getCells() {
        return cells;
    }

    public void setCells() {
        JSONGameRead jsonGameRead = new JSONGameRead();
        jsonGameRead.setStreets();
        Map<Integer, Street> streets = jsonGameRead.getStreets();
        cells.add(start);
        for (int i = 0; i < streets.size(); i++) {
            cells.add(streets.get(i + 1));
        }
    }
}
