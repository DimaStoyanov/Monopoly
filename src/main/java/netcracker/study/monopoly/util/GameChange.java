package netcracker.study.monopoly.util;

import netcracker.study.monopoly.game.Gamer;
import netcracker.study.monopoly.game.cells.Cell;
import netcracker.study.monopoly.game.cells.Street;

import java.util.List;

public class GameChange {

    private List<Gamer> gamersChange;
    private List<Cell> cellsChange;
    private List<Street> streetChange;

    public boolean addGamerChange(Gamer gamer) {
        try {
            gamersChange.add(gamer);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean addCellChange(Cell cell) {
        try {
            cellsChange.add(cell);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean addStreetChange(Street street) {
        try {
            streetChange.add(street);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Gamer getGamersChange(int i) {
        return gamersChange.get(i);
    }

    public Cell getCellsChange(int i) {
        return cellsChange.get(i);
    }

    public Street getStreetChange(int i) {
        return streetChange.get(i);
    }

    public int sizeGamersChange() {
        return streetChange.size();
    }

    public int sizeCellsChange() {
        return cellsChange.size();
    }

    public int sizeStreetChange() {
        return streetChange.size();
    }

    public void clear() {
        streetChange.clear();
        cellsChange.clear();
        gamersChange.clear();
    }
}
