package netcracker.study.monopoly.db.model.json;

import lombok.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "turnOf")
public class GameState {

    @Setter
    private UUID turnOf;

    private List<PlayerState> players;

    private List<CellState> field;


}
