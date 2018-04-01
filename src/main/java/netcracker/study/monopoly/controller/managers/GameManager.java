package netcracker.study.monopoly.controller.managers;

import netcracker.study.monopoly.controller.dto.Gamer;
import netcracker.study.monopoly.controller.dto.cells.Cell;
import netcracker.study.monopoly.controller.dto.cells.Street;
import netcracker.study.monopoly.db.DbManager;
import netcracker.study.monopoly.exceptions.EntryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
public class GameManager {

    private final static int FIELD_SIZE = 23;
    DbManager dbManager;

    @Autowired
    public GameManager(DbManager dbManager) {
        this.dbManager = dbManager;
    }

    public void firstStep(UUID gameId, UUID playerId) throws EntryNotFoundException {
        Random random = new Random();
        Gamer gamer = null;
        go(gamer);
        Cell cell = null;
        if (show(cell).equals("Street"))
            if (showStreet((Street) cell))
                pay(gamer, (Street) cell);
        if (show(cell).equals("Start"))
            startAction(gamer);
    }

    public void secondStep() {

    }

    private void pay(Gamer gamer, Street street) {
        Gamer owner = null;
        int money = street.getCost();
        gamer.setMoney(gamer.getMoney() - money);
        owner.setMoney(owner.getMoney() - money);
    }


    private String show(Cell cell) {
        return "Street";
    }


    private boolean showStreet(Street street) {
        return street.getOwner().equals(null);
    }

    private void startAction(Gamer gamer) {
        gamer.setMoney(gamer.getMoney() + 2000);
    }

    private void go(Gamer gamer) throws EntryNotFoundException {
        Random random = new Random();
        int position = gamer.getPosition() + random.nextInt(6) + random.nextInt(6) + 2;
        if (position > FIELD_SIZE) {
            startAction(gamer);
            gamer.setPosition(position - gamer.getPosition());
        } else
            gamer.setPosition(position);
    }


}
