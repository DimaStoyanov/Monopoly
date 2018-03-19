package netcracker.study.monopoly.game.cells;

import netcracker.study.monopoly.game.Gamer;

public class Start implements Cell {

    int money;
    int position;
    String name;

    @Override
    public void action(Gamer gamer) {
        gamer.sell(money);
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
