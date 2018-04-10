package netcracker.study.monopoly.models;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.SneakyThrows;
import netcracker.study.monopoly.models.entities.CellState;
import netcracker.study.monopoly.models.entities.Game;
import netcracker.study.monopoly.models.entities.Player;
import netcracker.study.monopoly.models.entities.PlayerState;

import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

import static netcracker.study.monopoly.models.entities.CellState.CellType.START;
import static netcracker.study.monopoly.models.entities.CellState.CellType.STREET;

public enum GameCreator {


    INSTANCE();

    private final String cellsPath = "src/main/resources/static/game/field";
    private final String playerConfigPath =
            "src/main/resources/static/game/gamers/gamer.json";

    private final Collection<CellState> cells;
    private final PlayerConfig playerConfig;
    private final Gson gson;

    GameCreator() {
        gson = new Gson();
        cells = initCells();
        playerConfig = initPlayerConfig();
    }

    @SneakyThrows
    private PlayerConfig initPlayerConfig() {
        FileReader json = new FileReader(new File(playerConfigPath));
        return gson.fromJson(json, PlayerConfig.class);
    }

    @SneakyThrows
    private Collection<CellState> initCells() {
        Map<Integer, CellState> cellsMap = new TreeMap<>();
        File cellsDir = new File(cellsPath);
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
        }
    }


    private List<CellState> getCells() {
        return cells.stream()
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
                    state.setMoney(playerConfig.getMoney());
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
