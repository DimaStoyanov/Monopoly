package netcracker.study.monopoly.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import netcracker.study.monopoly.controller.dto.cells.Cell;

import java.util.List;

@Data
@NoArgsConstructor
public class GameDto {
    @NonNull
    Gamer turnOf;
    @NonNull
    List<Gamer> players;
    @NonNull
    List<Cell> field;


}
