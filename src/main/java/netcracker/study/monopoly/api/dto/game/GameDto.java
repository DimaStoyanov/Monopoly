package netcracker.study.monopoly.api.dto.game;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import netcracker.study.monopoly.api.dto.game.cells.Cell;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class GameDto implements Serializable {
    @NonNull
    private Gamer turnOf;
    @NonNull
    private List<Gamer> players;
    @NonNull
    private List<Cell> field;
    @NonNull
    private UUID id;
    @NonNull
    private String currentState;

}
