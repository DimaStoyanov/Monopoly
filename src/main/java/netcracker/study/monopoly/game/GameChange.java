package netcracker.study.monopoly.game;

import lombok.Getter;
import netcracker.study.monopoly.game.cells.Street;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GameChange {

    private List<Gamer> gamersChange;
    private List<Street> streetChange;

    public GameChange() {
        gamersChange = new ArrayList<>();
        streetChange = new ArrayList<>();
    }

    public boolean addGamerChange(Gamer gamer) {
        try {
            gamersChange.add(gamer);
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

    public void clear() {
        streetChange.clear();
        gamersChange.clear();
    }
}
