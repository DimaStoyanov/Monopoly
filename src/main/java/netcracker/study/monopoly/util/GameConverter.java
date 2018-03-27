package netcracker.study.monopoly.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import netcracker.study.monopoly.db.model.Game;
import netcracker.study.monopoly.db.model.PlayerState;
import netcracker.study.monopoly.db.model.StreetState;
import netcracker.study.monopoly.game.GameChange;
import netcracker.study.monopoly.game.Gamer;
import netcracker.study.monopoly.game.PlayGame;
import netcracker.study.monopoly.game.cells.Cell;
import netcracker.study.monopoly.game.cells.Street;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Binds entities from a database with entities from the logic game
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GameConverter {

    /**
     * Applies changes from {@link PlayGame}, that stores in {@link GameChange} to {@link Game}
     *
     * @param dbGame     - existing old game state from database
     * @param gameChange - game changes in this turn
     * @return updated game state, that stores in database
     */
    public static Game toDb(Game dbGame, GameChange gameChange) {


        gameChange.getStreetChange().forEach(c -> {
            StreetState cell = dbGame.getField().get(c.getPosition());
            cell.setLevel(c.getLevel());
            cell.setCost(c.getCost());
            cell.setOwner(dbGame.getPlayerStates().get(c.getOwner().getOrder()));
        });

        gameChange.getGamersChange().forEach(g -> {
            PlayerState player = dbGame.getPlayerStates().get(g.getOrder());
            player.setPosition(g.getPosition());
            player.setMoney(g.getMoney());
        });
        return dbGame;
    }

    /**
     * Converts {@link Game} to {@link PlayGame}
     *
     * @param game Actual game state from database
     * @return Actual game state in logic entity
     */
    public static PlayGame fromDb(Game game) {
        Map<String, Gamer> gamerMap = game.getPlayerStates().stream()
                .map(GameConverter::toGamer)
                .collect(Collectors.toMap(Gamer::getName, g -> g));
        List<Cell> field = game.getField().stream()
                .map(s -> toStreet(s, gamerMap))
                .collect(Collectors.toList());
        return new PlayGame(new ArrayList<>(gamerMap.values()), field);
    }

    private static Gamer toGamer(PlayerState playerState) {
        Gamer gamer = new Gamer(playerState.getPlayer().getNickname(), playerState.getOrder());
        gamer.setPosition(playerState.getPosition());
        gamer.setCanGo(playerState.isCanGo());
        gamer.setMoney(playerState.getMoney());
        return gamer;
    }

    private static Cell toStreet(StreetState dbStreet, Map<String, Gamer> gamerMap) {
        Street gameStreet = new Street();
        gameStreet.setLevel(dbStreet.getLevel());
        gameStreet.setName(dbStreet.getName());
        gameStreet.setPosition(dbStreet.getPosition());
        gameStreet.setCost(dbStreet.getCost());
        gameStreet.setOwner(gamerMap.get(dbStreet.getOwner().getPlayer().getNickname()));
        return gameStreet;
    }

}
