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
    public void action(Gamer gamer) {
        if (owner == null) {
            if (toBuy){
                buy(gamer);
            }
        }else {
            pay(gamer);
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
