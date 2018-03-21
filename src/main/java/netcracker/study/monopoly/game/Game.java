package netcracker.study.monopoly.game;

import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Game {
    Gson gson = new Gson();

    List<Gamer> gamers = new ArrayList<>();
//    List<Cell>

    BufferedReader br;

    File myFolder = new File("src/main/java/netcracker/study/monopoly/game/game/gamers");
    File[] files = myFolder.listFiles();

    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("src/main/java/netcracker/study/monopoly/game/game/gamers/gamer1.json");
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        Game game = new Game();
        Gamer gamer = game.gson.fromJson(br, Gamer.class);
        System.out.println(gamer.money);
    }

    public void start(int gamersCount) {
        for (int j = 0; j < gamersCount; j++) {
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(files[j])));
                gamers.add(gson.fromJson(br, Gamer.class));
                gamers.get(j).setPosition(0);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
