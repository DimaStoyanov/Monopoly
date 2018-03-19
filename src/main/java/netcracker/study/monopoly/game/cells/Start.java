package netcracker.study.monopoly.game.cells;

import netcracker.study.monopoly.game.Gamer;

public class Start implements Cell {

    int salary;
    int position;
    String name;

    @Override
    public void action(Gamer gamer) {
        gamer.sell(salary);
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
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
