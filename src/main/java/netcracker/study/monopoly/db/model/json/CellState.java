package netcracker.study.monopoly.db.model.json;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "owner")
public class CellState implements Serializable {

    private Integer position;

    @Setter
    private UUID owner;

    @Setter
    private Integer level;
}
