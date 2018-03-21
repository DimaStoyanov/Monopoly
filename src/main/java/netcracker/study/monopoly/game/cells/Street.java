package netcracker.study.monopoly.game.cells;

import netcracker.study.monopoly.game.Gamer;

public class Street implements Cell{

    int cost;
    Gamer owner;
    String Name;
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
