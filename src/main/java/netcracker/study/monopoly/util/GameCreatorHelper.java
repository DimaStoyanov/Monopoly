package netcracker.study.monopoly.util;

import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import netcracker.study.monopoly.db.model.Game;
import netcracker.study.monopoly.db.model.Player;
import netcracker.study.monopoly.db.model.PlayerState;
import netcracker.study.monopoly.db.model.StreetState;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates {@link Game} with specified list of {@link Player}
 * Store streets in pool, and clone them after every requests to prevent repeated reading from file.
 */
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GameCreatorHelper {


    private final static String streetsPathName = "src/main/resources/game/streets";
    private final static List<StreetState> streets = initStreets();
    private final static String playerConfigPath = "src/main/resources/game/gamers/gamer.json";
    private final static int playerStartMoney = initPlayerMoney();

    @SneakyThrows
    private static int initPlayerMoney() {
        Gson gson = new Gson();
        PlayerConfig config = gson.fromJson(new FileReader(new File(playerConfigPath)), PlayerConfig.class);
        return config.getMoney();
    }

    @SneakyThrows
    private static List<StreetState> initStreets() {
        Gson gson = new Gson();
        File[] files = new File(streetsPathName).listFiles();
        List<StreetState> streets = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                streets.add(gson.fromJson(new FileReader(file), StreetState.class));
            }
        }
        return streets;
    }

    @SneakyThrows
    private static List<StreetState> getStreets() {
        return GameCreatorHelper.streets.stream()
                .map(s -> new StreetState(s.getPosition(), s.getCost(), s.getName()))
                .collect(Collectors.toList());
    }

    private static List<PlayerState> getGamers(List<Player> players) {
        return players.stream()
                .map(p -> new PlayerState(playerStartMoney, players.indexOf(p), p))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    public static Game createGame(List<Player> players) {
        List<PlayerState> playerStates = getGamers(players);
        return new Game(playerStates, players.get(0), getStreets());
    }


    private static class PlayerConfig {
        @Getter
        private int money;
    }

}
