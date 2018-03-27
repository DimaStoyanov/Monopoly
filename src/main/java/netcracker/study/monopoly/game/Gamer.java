package netcracker.study.monopoly.game;

import lombok.Getter;
import lombok.Setter;

import java.util.Random;

public class Gamer {
    @Getter
    int order;
    @Getter
    @Setter
    int position;
    @Getter
    private String name;
    @Getter
    @Setter
    private int money;
    @Getter
    @Setter
    private boolean canGo;
    private int first;
    private int second;
    private Random random = new Random();


    public Gamer(String name, int order) {
        this.name = name;
        this.order = order;
        position = 0;
    }


    public void go() {
        first = random.nextInt(5) + 1;
        second = random.nextInt(5) + 1;
        position = position + first + second;
    }

    public void action() {

    }

    public void sell(int i) {
        money += i;
    }

    public void buy(int i) {
        if (money > i) {
            money -= i;
        } else {
            System.out.println("You can't buy this street");
        }
    }

    public void jailAction(boolean b) {
        canGo = b;
    }

    public void pay(int i) {
        if (money > i) {
            money -= i;
        } else {
            System.out.println("You can't pay");
        }
    }
}
