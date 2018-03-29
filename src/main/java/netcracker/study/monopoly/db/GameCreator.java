package netcracker.study.monopoly.db;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.SneakyThrows;
import netcracker.study.monopoly.db.model.CellState;
import netcracker.study.monopoly.db.model.Game;
import netcracker.study.monopoly.db.model.Player;
import netcracker.study.monopoly.db.model.PlayerState;

import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

import static netcracker.study.monopoly.db.model.CellState.CellType.START;
import static netcracker.study.monopoly.db.model.CellState.CellType.STREET;

public enum GameCreator {


    INSTANCE();

    private final String CELLS_PATH = "src/main/resources/game/field";
    private final String PLAYER_CONFIG_PATH = "src/main/resources/game/gamers/gamer.json";

    private final Collection<CellState> CELLS;
    private final PlayerConfig PLAYER_CONFIG;
    private final Gson gson;

    GameCreator() {
        gson = new Gson();
        CELLS = initCells();
        PLAYER_CONFIG = initPlayerConfig();
    }

    @SneakyThrows
    private PlayerConfig initPlayerConfig() {
        return gson.fromJson(new FileReader(new File(PLAYER_CONFIG_PATH)), PlayerConfig.class);
    }

    @SneakyThrows
    private Collection<CellState> initCells() {
        Map<Integer, CellState> cellsMap = new TreeMap<>();
        File cellsDir = new File(CELLS_PATH);
        putCells(cellsDir, cellsMap);
        return cellsMap.values();
    }


    @SneakyThrows
    private void putCells(File file, Map<Integer, CellState> cellsMap) {
        if (file.isDirectory()) {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                putCells(f, cellsMap);
            }
        } else if (file.isFile()) {
            CellState cell = gson.fromJson(new FileReader(file), CellState.class);
            // TODO fix json file positions and delete this if (to read all cells)
            if (cell.getType() == STREET || cell.getType() == START) {
                cellsMap.put(cell.getPosition(), cell);
            }
        } else {
            throw new RuntimeException("Irregular file in resources " + file.getPath());
        }
    }


    private List<CellState> getCells() {
        return CELLS.stream()
                .map(s -> {
                    CellState state = new CellState(s.getPosition(), s.getName(), s.getType());
                    state.setCost(s.getCost());
                    return state;
                })
                .collect(Collectors.toList());
    }

    private List<PlayerState> getGamers(List<Player> players) {
        return players.stream()
                .map(p -> {
                    PlayerState state = new PlayerState(players.indexOf(p), p);
                    state.setMoney(PLAYER_CONFIG.getMoney());
                    return state;
                })
                .collect(Collectors.toList());
    }

    @SneakyThrows
    public Game createGame(List<Player> players) {
        List<PlayerState> gamers = getGamers(players);
        return new Game(gamers, getCells());
    }


    @Getter
    private class PlayerConfig {
        private int money;
    }

}
