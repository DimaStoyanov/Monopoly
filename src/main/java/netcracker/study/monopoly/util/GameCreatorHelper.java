package netcracker.study.monopoly.util;

import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import netcracker.study.monopoly.db.model.CellState;
import netcracker.study.monopoly.db.model.Game;
import netcracker.study.monopoly.db.model.Player;
import netcracker.study.monopoly.db.model.PlayerState;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static netcracker.study.monopoly.db.model.CellState.CellType.STREET;

/**
 * Creates {@link Game} with specified list of {@link Player}
 * Store STREETS in pool, and clone them after every requests to prevent repeated reading from file.
 */
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GameCreatorHelper {


    private final static String STREETS_PATH = "src/main/resources/game/streets";
    private final static List<CellState> STREETS = initStreets();
    private final static String PLAYER_CONFIG_PATH = "src/main/resources/game/gamers/gamer.json";
    private final static PlayerConfig PLAYER_CONFIG = initPlayerConfig();

    @SneakyThrows
    private static PlayerConfig initPlayerConfig() {
        Gson gson = new Gson();
        return gson.fromJson(new FileReader(new File(PLAYER_CONFIG_PATH)), PlayerConfig.class);
    }

    @SneakyThrows
    private static List<CellState> initStreets() {
        Gson gson = new Gson();
        File[] files = new File(STREETS_PATH).listFiles();
        List<CellState> streets = new ArrayList<>();
        if (files == null)
            throw new RuntimeException(String.format("Package %s not found", STREETS_PATH));
        for (File file : files) {
            streets.add(gson.fromJson(new FileReader(file), CellState.class));
        }
        return streets;
    }

    @SneakyThrows
    private static List<CellState> getStreets() {
        List<CellState> cells = STREETS.stream()
                .map(s -> {
                    CellState state = new CellState(s.getPosition(), s.getName(), STREET);
                    state.setCost(s.getCost());
                    return state;
                })
                .collect(Collectors.toList());
        return cells;
        // TODO add start, jail, etc
    }

    private static List<PlayerState> getGamers(List<Player> players) {
        return players.stream()
                .map(p -> {
                    PlayerState state = new PlayerState(players.indexOf(p), p);
                    state.setMoney(PLAYER_CONFIG.getMoney());
                    return state;
                })
                .collect(Collectors.toList());
    }

    @SneakyThrows
    public static Game createGame(List<Player> players) {
        List<PlayerState> playerStates = getGamers(players);
        return new Game(playerStates, getStreets());
    }


    private static class PlayerConfig {
        @Getter
        private int money;
    }

}
