package netcracker.study.monopoly.db.model.json;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "owner")
public class CellState {

    private Integer position;

    @Setter
    private UUID owner;

    @Setter
    private Integer level;
}
