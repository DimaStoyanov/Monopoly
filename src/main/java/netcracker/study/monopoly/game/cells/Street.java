package netcracker.study.monopoly.game.cells;

import lombok.Getter;
import netcracker.study.monopoly.game.Gamer;

@Getter
public class Street implements Cell{

    int cost;
    Gamer owner;
    String name;
    boolean toBuy;
    int position;

    @Override
    public String action(Gamer gamer) {
        if (owner == null) {
            buy(gamer);
            return "Вы приобрели " + name;
        }else {
            pay(gamer);
            return "Вы оплатили за проживание на " + name;
        }
    }

    @Override
    public String show() {
        if (owner == null) {
            return "Вы можете приобрести " + name;
        } else {
            return "Вы должны оплатить за проживание на " + name;
        }
    }

    public void buy(Gamer gamer){
        owner = gamer;
        gamer.buy(cost);
    }

    public void pay(Gamer gamer){
        owner = gamer;
        gamer.pay(cost);
    }
}
