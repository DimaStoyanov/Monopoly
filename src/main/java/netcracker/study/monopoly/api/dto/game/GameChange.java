package netcracker.study.monopoly.api.dto.game;

import lombok.Data;
import netcracker.study.monopoly.api.dto.game.cells.Street;
import netcracker.study.monopoly.models.entities.Game.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class GameChange {
    private UUID turnOf;
    private List<Gamer> gamersChange = new ArrayList<>();
    private Street streetChange;
    private List<String> changeDescriptions = new ArrayList<>();
    private GameState currentState;

    public void addGamerChange(Gamer gamer) {
        gamersChange.add(gamer);
    }

    public void addChangeDescription(String description) {
        changeDescriptions.add(description);
    }
}

