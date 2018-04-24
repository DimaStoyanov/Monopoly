package netcracker.study.monopoly.models;

import com.google.gson.Gson;
import lombok.Cleanup;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import netcracker.study.monopoly.api.dto.game.cells.Cell;
import netcracker.study.monopoly.api.dto.game.cells.Start;
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
    private final List<Start> dtoCells;
    private final PlayerConfig playerConfig;
    private final Gson gson;

    private final Map<Integer, Start> cellsMap;


    GameCreator() {
        gson = new Gson();
        cellsMap = new TreeMap<>();
        cells = initCells();
        dtoCells = initDtoCells();
        playerConfig = initPlayerConfig();
    }


    @SneakyThrows
    private PlayerConfig initPlayerConfig() {
        FileReader json = new FileReader(new File(playerConfigPath));
        return gson.fromJson(json, PlayerConfig.class);
    }

    @SneakyThrows
    private Collection<CellState> initCells() {
        Map<Integer, CellState> cellsStateMap = new TreeMap<>();
        File cellsDir = new File(cellsPath);
        putCells(cellsDir, cellsMap, cellsStateMap);
        return cellsStateMap.values();
    }


    private List<Start> initDtoCells() {
        return new ArrayList<>(cellsMap.values());
    }


    @SneakyThrows
    private void putCells(File file, Map<Integer, Start> cellsMap, Map<Integer, CellState> cellStateMap) {
        if (file.isDirectory()) {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                putCells(f, cellsMap, cellStateMap);
            }
        } else if (file.isFile()) {
            @Cleanup
            FileReader json = new FileReader(file);
            CellState cellState = gson.fromJson(json, CellState.class);
            json = new FileReader(file);
            Start cell = gson.fromJson(json, Start.class);
            if (cellState.getType() == START || cellState.getType() == STREET) {
                cellStateMap.put(cellState.getPosition(), cellState);
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

    public Cell getInitCell(@NonNull Integer position) {
        return dtoCells.get(position);
    }


    @Getter
    private class PlayerConfig {
        private int money;
    }

}
