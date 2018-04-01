package netcracker.study.monopoly.controller.managers;

import netcracker.study.monopoly.controller.dto.GameDto;
import netcracker.study.monopoly.controller.dto.Gamer;
import netcracker.study.monopoly.controller.dto.cells.Street;
import netcracker.study.monopoly.db.DbManager;
import netcracker.study.monopoly.db.model.CellState;
import netcracker.study.monopoly.db.model.PlayerState;
import netcracker.study.monopoly.exceptions.EntryNotFoundException;
import netcracker.study.monopoly.util.DbDtoConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class GameManager {

    private final static int FIELD_SIZE = 23;
    DbManager dbManager;

    final
    DbDtoConverter converter;

    @Autowired
    public GameManager(DbManager dbManager, DbDtoConverter converter) {
        this.dbManager = dbManager;
        this.converter = converter;
    }

    public GameDto create(List<UUID> gamerIds) throws EntryNotFoundException {
        return converter.gameToDto(dbManager.createGame(gamerIds));
    }

    public GameDto getGame(UUID gameId) throws EntryNotFoundException {
        return converter.gameToDto(dbManager.getGame(gameId));
    }

    public void firstStep(UUID gameId, UUID playerId) throws EntryNotFoundException {
        PlayerState ps = dbManager.getPlayer(playerId);
        Gamer gamer = converter.playerToDto(ps);
        go(gamer);
        CellState cs = dbManager.getCell(gameId, gamer.getPosition());
        switch (cs.getType()) {
            case STREET:
                Street street = converter.streetToDto(cs);
                if (hasOwner(street))
                    pay(gamer, street);
                break;
            case START:
                startAction(gamer);
                break;
        }
        dbManager.updatePlayer(converter.gamerToDb(gamer, ps));
    }

    public void streetStep(UUID gameId, UUID playerId) throws EntryNotFoundException {
        PlayerState ps = dbManager.getPlayer(playerId);
        Gamer gamer = converter.playerToDto(ps);
        Street street = converter.streetToDto(dbManager.getCell(gameId, gamer.getPosition()));
        buy(gamer, street);
    }

    private void pay(Gamer gamer, Street street) throws EntryNotFoundException {
        Gamer owner = street.getOwner();
        PlayerState ps = dbManager.getPlayer(owner.getId());
        int money = street.getCost();
        gamer.setMoney(gamer.getMoney() - money);
        owner.setMoney(owner.getMoney() + money);
        dbManager.updatePlayer(converter.gamerToDb(owner, ps));
    }

    private boolean buy(Gamer gamer, Street street) {
        if (street.getOwner() != null)
            return false;
        int cost = street.getCost();
        if (gamer.getMoney() < cost)
            return false;
        gamer.setMoney(gamer.getMoney() - cost);
        street.setOwner(gamer);
        return true;
    }


    private boolean hasOwner(Street street) {
        return street.getOwner() == null;
    }

    private void startAction(Gamer gamer) {
        gamer.setMoney(gamer.getMoney() + 2000);
    }

    private void go(Gamer gamer) {
        Random random = new Random();
        int position = gamer.getPosition() + random.nextInt(6) + random.nextInt(6) + 2;
        if (position > FIELD_SIZE) {
            startAction(gamer);
            gamer.setPosition(position - gamer.getPosition());
        } else
            gamer.setPosition(position);
    }


}
