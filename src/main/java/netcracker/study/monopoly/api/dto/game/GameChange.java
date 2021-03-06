package netcracker.study.monopoly.api.dto.game;

import lombok.Data;
import netcracker.study.monopoly.api.dto.game.cells.Street;
import netcracker.study.monopoly.models.entities.Game.GameState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class GameChange implements Serializable {
    private UUID turnOf;
    private List<Gamer> gamersChange = new ArrayList<>();
    private List<Street> streetChanges = new ArrayList<>();
    private List<String> changeDescriptions = new ArrayList<>();
    private GameState currentState;

    public void addGamerChange(Gamer gamer) {
        gamersChange.add(gamer);
    }

    public void addStreetChange(Street street) {
        streetChanges.add(street);
    }

    public void addChangeDescription(String description) {
        changeDescriptions.add(description);
    }
}

