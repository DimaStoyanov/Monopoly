package netcracker.study.monopoly.game.cells;

import netcracker.study.monopoly.game.Gamer;

public class Jail implements Cell {

    int money;
    private boolean toImprison = false;
    int position;

    @Override
    public void action(Gamer gamer) {
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public boolean isToImprison() {
        return toImprison;
    }

    public void setToImprison(boolean toImprison) {
        this.toImprison = toImprison;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
