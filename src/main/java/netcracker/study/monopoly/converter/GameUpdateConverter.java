package netcracker.study.monopoly.converter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import netcracker.study.monopoly.db.model.Game;
import netcracker.study.monopoly.game.Gamer;
import netcracker.study.monopoly.game.PlayGame;
import netcracker.study.monopoly.game.cells.Cell;

import java.util.List;

/**
 * Convert updates from {@link PlayGame} to {@link Game}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GameUpdateConverter {

    public static Game updateDBEntry(Game dbGame, List<Gamer> playersUpdate, List<Cell> cellsUpdate) {
        // TODO

        return dbGame;
    }
}
