package netcracker.study.monopoly.util;

import netcracker.study.monopoly.db.model.CellState;
import netcracker.study.monopoly.db.model.Game;
import netcracker.study.monopoly.db.model.Player;
import netcracker.study.monopoly.db.model.PlayerState;
import netcracker.study.monopoly.game.Gamer;
import netcracker.study.monopoly.game.cells.Street;
import netcracker.study.monopoly.game.field.PlayGame;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class GameConverter {

    private GameConverter() {
    }

    public static Game toGameDB(PlayGame playGame) {
        List<PlayerState> playerStates = new ArrayList<>();
        List<CellState> cellStates = new ArrayList<>();

        for (int i = 0; i < playGame.getGamers().size(); i++) {
            playerStates.add(GameConverter.toPlayerState(playGame.getGamers().get(i)));
        }
        for (int i = 0; i < playGame.getField().getCells().size(); i++) {
            if (playGame.getField().getCells().get(i) instanceof Street) {
                cellStates.add(GameConverter.toCellState((Street) playGame.getField().getCells().get(i)));
            }
        }
        Game game = new Game();
        return game;
    }

    public static PlayerState toPlayerState(Gamer gamer) {
        Date date = new Date();
        Player player = new Player(gamer.getName(), date);
        PlayerState playerState = new PlayerState(gamer.getMoney(), gamer.getOrder(), player);
        return playerState;
    }

    public static CellState toCellState(Street street) {
        CellState cellState = new CellState(street.getPosition());
        cellState.setLevel(street.getLevel());
        cellState.setOwner(GameConverter.toPlayer(street.getOwner()));
        return cellState;
    }

    public static Player toPlayer(Gamer gamer) {
        Date date = new Date();
        Player player = new Player(gamer.getName(), date);
        return player;
    }
}
