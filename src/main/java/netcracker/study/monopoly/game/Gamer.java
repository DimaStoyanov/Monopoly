package netcracker.study.monopoly.game;

public class Gamer {

    String name;
    int money;
    boolean canGo;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    int position;

    public boolean isCanGo() {
        return canGo;
    }

    public void setCanGo(boolean canGo) {
        this.canGo = canGo;
    }

    public void go(){
    }

    public void action(){

    }

    public void sell(int i){
        money+=i;
    }

    public void buy(int i){
        if(money > i){
            money-=i;
        } else {
            System.out.println("You can't buy this street");
        }
    }

    public void jailAction(boolean b){
        canGo = b;
    }

    public void pay(int i){
        if(money > i){
            money-=i;
        } else {
            System.out.println("You can't pay");
        }
    }
}
