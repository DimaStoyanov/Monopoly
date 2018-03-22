package netcracker.study.monopoly.util;

import com.google.gson.Gson;
import netcracker.study.monopoly.game.Gamer;
import netcracker.study.monopoly.game.cells.Street;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONRead {

    Gson gson = new Gson();

    List<Gamer> gamers = new ArrayList<>();
    Map<Integer, Street> cells = new HashMap<>();
    Street street;

    BufferedReader br;

    File gamerFolder = new File("/src/main/resources/game/gamers");
    File[] gamerFiles = gamerFolder.listFiles();

    File cellFolder = new File("src/main/resources/game/streets");
    File[] cellFiles = cellFolder.listFiles();

    public static void main(String[] args) {
        JSONRead jsonRead = new JSONRead();
        Map<Integer, Street> cells;
        cells = jsonRead.getCells();

        for (int i = 1; i < cells.size(); i++) {
            System.out.println(cells.get(i).getName());
        }

    }

    public List<Gamer> getGamers(int gamersCount) {
        for (int j = 0; j < gamersCount; j++) {
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(gamerFiles[j])));
                gamers.add(gson.fromJson(br, Gamer.class));
                gamers.get(j).setPosition(0);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return gamers;
    }

    public Map<Integer, Street> getCells() {
        for (int j = 0; j < cellFiles.length; j++) {
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(cellFiles[j])));
                street = gson.fromJson(br, Street.class);
                cells.put(street.getPosition(), street);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return cells;
    }
}
