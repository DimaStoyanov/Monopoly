package netcracker.study.monopoly.managers;

import lombok.extern.log4j.Log4j2;
import netcracker.study.monopoly.api.dto.game.GameChange;
import netcracker.study.monopoly.api.dto.game.GameDto;
import netcracker.study.monopoly.api.dto.game.Gamer;
import netcracker.study.monopoly.api.dto.game.cells.Street;
import netcracker.study.monopoly.converters.CellConverter;
import netcracker.study.monopoly.converters.GameConverter;
import netcracker.study.monopoly.converters.PlayerConverter;
import netcracker.study.monopoly.exceptions.CellNotFoundException;
import netcracker.study.monopoly.exceptions.GameNotFoundException;
import netcracker.study.monopoly.exceptions.PlayerNotFoundException;
import netcracker.study.monopoly.models.GameCreator;
import netcracker.study.monopoly.models.entities.CellState;
import netcracker.study.monopoly.models.entities.Game;
import netcracker.study.monopoly.models.entities.Player;
import netcracker.study.monopoly.models.entities.PlayerState;
import netcracker.study.monopoly.models.repositories.CellStateRepository;
import netcracker.study.monopoly.models.repositories.GameRepository;
import netcracker.study.monopoly.models.repositories.PlayerRepository;
import netcracker.study.monopoly.models.repositories.PlayerStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.lang.String.format;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Service
@Log4j2
@Transactional
// TODO return GameChange from most of methods
public class GameManager {

    private static final int FIELD_SIZE = 23;
    private final Random random = new Random();
    @Autowired
    private PlayerStateRepository playerStateRepository;
    @Autowired
    private CellStateRepository cellStateRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private GameConverter gameConverter;
    @Autowired
    private PlayerConverter playerConverter;
    @Autowired
    private CellConverter cellConverter;

    public GameDto create(Collection<UUID> playerIds) {
        log.trace("Creating game with ids: " + playerIds);
        List<Player> players = (List<Player>) playerRepository.findAllById(playerIds);
        if (players.size() != playerIds.size()) {
            throw new PlayerNotFoundException();
        }

        Game game = GameCreator.INSTANCE.createGame(players);
        log.trace("Saving game " + game);
        gameRepository.save(game);
        return gameConverter.toDto(game);
    }


    public GameDto getGame(UUID gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow(() ->
                new GameNotFoundException(gameId));
        log.trace("Get game " + game);
        return gameConverter.toDto(game);
    }

    public void firstStep(UUID gameId, UUID playerId) {
        PlayerState player = playerStateRepository.findById(playerId).orElseThrow(() ->
                new PlayerNotFoundException(playerId));
        changePlayerPosition(player);
        Integer position = player.getPosition();
        CellState cell = cellStateRepository.findByGameIdAndPosition(gameId, position)
                .orElseThrow(() -> new CellNotFoundException(gameId, position));

        switch (cell.getType()) {
            case START:
                giveSalary(player);
                break;
            case FLIGHT:
                break;
            case JAIL:
                break;
            default:
                if (hasOwner(cell)) {
                    payToOwner(player, cell);
                }
                break;
        }
        playerStateRepository.save(player);
    }

    public GameChange streetStep(UUID gameId, UUID playerId) {
        PlayerState playerState = playerStateRepository.findById(playerId).orElseThrow(() ->
                new PlayerNotFoundException(playerId));
        Integer position = playerState.getPosition();
        CellState cell = cellStateRepository.findByGameIdAndPosition(gameId, position)
                .orElseThrow(() -> new CellNotFoundException(gameId, position));
        buyStreet(playerState, cell);

        GameChange gameChange = new GameChange();
        Gamer gamer = playerConverter.toDto(playerState);
        gameChange.setGamerChange(gamer);
        Street street = cellConverter.toStreet(cell);
        gameChange.setStreetChange(street);
        return gameChange;
    }

    public void flightStep(UUID gameId, UUID playerId, Integer newPosition) {
        PlayerState playerState = playerStateRepository.findById(playerId).orElseThrow(() ->
                new PlayerNotFoundException(playerId));
        Integer position = playerState.getPosition();
        CellState cell = cellStateRepository.findByGameIdAndPosition(gameId, position)
                .orElseThrow(() -> new CellNotFoundException(gameId, position));

        flight(playerState, cell, newPosition);

        firstStep(gameId, playerId);
    }

    private void payToOwner(PlayerState playerState, CellState street) {
        PlayerState owner = street.getOwner();
        int money = street.getCost();
        // TODO: If not enough money - bankrupt?
        playerState.setMoney(playerState.getMoney() - money);
        owner.setMoney(owner.getMoney() + money);
        playerStateRepository.save(owner);
    }

    private boolean buyStreet(PlayerState playerState, CellState street) {
        if (hasOwner(street)) {
            return false;
        }
        int cost = street.getCost();
        if (playerState.getMoney() < cost) {
            return false;
        }
        playerState.setMoney(playerState.getMoney() - cost);
        street.setOwner(playerState);
        playerStateRepository.save(playerState);
        cellStateRepository.save(street);
        return true;
    }

    private boolean flight(PlayerState playerState, CellState cellState, Integer position) {
        playerState.setPosition(position);
        playerState.setMoney(playerState.getMoney() - cellState.getCost());
        return true;
    }


    private boolean hasOwner(CellState street) {
        return street.getOwner() == null;
    }

    private void giveSalary(PlayerState playerState) {
        playerState.setMoney(playerState.getMoney() + 2000);
    }

    private void changePlayerPosition(PlayerState playerState) {
        int step = random.nextInt(6) + random.nextInt(6) + 2;
        int position = playerState.getPosition() + step;
        if (position > FIELD_SIZE) {
            giveSalary(playerState);
            playerState.setPosition(position - playerState.getPosition());
        } else {
            playerState.setPosition(position);
        }
    }


    public void finishGame(UUID gameId, UUID winnerStateId) {
        Game game = gameRepository.findById(gameId).orElseThrow(() ->
                new GameNotFoundException(gameId));
        if (game.isFinished()) {
            throw new IllegalStateException(format("Game %s has already finished", game));
        }

        Player winner = playerStateRepository.findById(winnerStateId)
                .orElseThrow(() -> new PlayerNotFoundException(winnerStateId)).getPlayer();
        game.setWinner(winner);
        game.setFinishedAt(new Date());
        game.getWinner().getStat().incrementTotalWins();
        game.getPlayerStates().forEach(p -> {
            p.getPlayer().getStat().incrementTotalGames();
            p.getPlayer().getStat().addTotalScore(p.getScore());
        });
        game.setFinished(true);
    }


}
