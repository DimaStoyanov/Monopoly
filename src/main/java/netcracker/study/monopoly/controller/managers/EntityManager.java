package netcracker.study.monopoly.controller.managers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import netcracker.study.monopoly.db.model.CellState;
import netcracker.study.monopoly.db.model.PlayerState;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EntityManager {


    public static UUID createGame() {
        // TODO
        return null;
    }

    public static CellState getCell(UUID cellId) {
        // TODO
        return null;
    }

    public static PlayerState getPlayer(UUID playerId) {
        // TODO
        return null;
    }

    public static UUID getTurnOf(UUID gameId) {
        // TODO
        return null;
    }

    public static void finishGame(UUID gameId) {
        // TODO
    }


}
