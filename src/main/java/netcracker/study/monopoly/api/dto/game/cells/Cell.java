package netcracker.study.monopoly.api.dto.game.cells;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;

@Data
@NoArgsConstructor
public abstract class Cell implements Serializable {
    @NonNull
    String type;

    @NonNull
    String name;

    @NonNull
    Integer position;

    @NonNull
    Integer cost;

    @NonNull
    String imgPath;

    @NonNull
    Double[][] cellCoordinates;

    @NonNull
    Double[][] routeCoordinates;

}
