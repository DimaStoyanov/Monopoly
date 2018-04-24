package netcracker.study.monopoly.api.dto.game.cells;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;

@Data
@NoArgsConstructor
public abstract class Cell implements Serializable {
    @NonNull
    private String type;

    @NonNull
    private String name;

    @NonNull
    private Integer position;

    @NonNull
    private Integer cost;

    @NonNull
    private String imgPath;

    @NonNull
    private Double[][] cellCoordinates;

    @NonNull
    private Double[][] routeCoordinates;

}
