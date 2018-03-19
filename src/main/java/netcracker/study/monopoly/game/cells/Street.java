package netcracker.study.monopoly.game.cells;

import netcracker.study.monopoly.game.Gamer;
import netcracker.study.monopoly.game.resources.Own;

public class Street implements Cell{

    int value;
    Gamer owner;
    String Name;
    Own own;
    boolean toBuy;
    int position;

    @Override
    public void action(Gamer gamer) {
        if(owner.equals(null)){
            if (toBuy){
                buy(gamer);
            }
        }else {
            pay(gamer);
        }
    }

    public void buy(Gamer gamer){
        owner = gamer;
        gamer.buy(value);
    }

    public void pay(Gamer gamer){
        owner = gamer;
        gamer.pay(value);
    }
}
