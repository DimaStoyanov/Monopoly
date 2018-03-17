package netcracker.study.monopoly.db.model.json;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "id")
public class PlayerState {

    private UUID id;

    @Setter
    private int position;

    @Setter
    private int money;

    @Setter
    private boolean canGo;

}
