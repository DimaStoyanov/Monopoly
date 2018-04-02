package netcracker.study.monopoly.controller.managers;

import netcracker.study.monopoly.controller.dto.GameChange;
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

    private final DbManager dbManager;
    private final DbDtoConverter converter;
    private final Random random = new Random();
    private int fieldSize = 23;

    @Autowired
    public GameManager(DbManager dbManager, DbDtoConverter converter) {
        this.dbManager = dbManager;
        this.converter = converter;
    }

    public GameDto create(List<UUID> gamerIds) throws EntryNotFoundException {
        GameDto gameDto = converter.gameToDto(dbManager.createGame(gamerIds));
        fieldSize = gameDto.getField().size();
        return gameDto;
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

    public GameChange streetStep(UUID gameId, UUID playerId) throws EntryNotFoundException {
        PlayerState ps = dbManager.getPlayer(playerId);
        Gamer gamer = converter.playerToDto(ps);
        CellState cell = dbManager.getCell(gameId, gamer.getPosition());
        Street street = converter.streetToDto(cell);
        buy(ps, gamer, street, cell);

        GameChange gameChange = new GameChange();
        gameChange.setGamerChange(gamer);
        gameChange.setStreetChange(street);
        return gameChange;
    }

    private void pay(Gamer gamer, Street street) throws EntryNotFoundException {
        Gamer owner = street.getOwner();
        PlayerState ps = dbManager.getPlayer(owner.getId());
        int money = street.getCost();
        // TODO: If not enough money - bankrupt?
        gamer.setMoney(gamer.getMoney() - money);
        owner.setMoney(owner.getMoney() + money);
        dbManager.updatePlayer(converter.gamerToDb(owner, ps));
    }

    private boolean buy(PlayerState ps, Gamer gamer, Street street, CellState cell)
            throws EntryNotFoundException {
        if (street.getOwner() != null)
            return false;
        int cost = street.getCost();
        if (gamer.getMoney() < cost)
            return false;
        gamer.setMoney(gamer.getMoney() - cost);
        street.setOwner(gamer);
        dbManager.updateCell(converter.streetToDb(street, cell, ps));
        dbManager.updatePlayer(converter.gamerToDb(gamer, ps));
        return true;
    }


    private boolean hasOwner(Street street) {
        return street.getOwner() == null;
    }

    private void startAction(Gamer gamer) {
        gamer.setMoney(gamer.getMoney() + 2000);
    }

    private void go(Gamer gamer) {
        int position = gamer.getPosition() + random.nextInt(6) + random.nextInt(6) + 2;
        if (position > fieldSize) {
            startAction(gamer);
            gamer.setPosition(position - gamer.getPosition());
        } else
            gamer.setPosition(position);
    }


}
