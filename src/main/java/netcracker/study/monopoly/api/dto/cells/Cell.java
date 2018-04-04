package netcracker.study.monopoly.api.dto.cells;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public abstract class Cell {
    @NonNull
    String name;

    @NonNull
    Integer position;

    @NonNull
    Integer cost;

}
