package netcracker.study.monopoly.managers;

import lombok.extern.log4j.Log4j2;
import netcracker.study.monopoly.api.dto.game.GameChange;
import netcracker.study.monopoly.api.dto.game.GameDto;
import netcracker.study.monopoly.api.dto.game.Gamer;
import netcracker.study.monopoly.api.dto.game.cells.Street;
import netcracker.study.monopoly.converters.CellConverter;
import netcracker.study.monopoly.converters.GameConverter;
import netcracker.study.monopoly.converters.PlayerConverter;
import netcracker.study.monopoly.exceptions.*;
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

    private GameCreator gameCreator = GameCreator.INSTANCE;

    public GameDto create(Collection<UUID> playerIds) {
        log.trace("Creating game with ids: " + playerIds);
        List<Player> players = (List<Player>) playerRepository.findAllById(playerIds);
        if (players.size() != playerIds.size()) {
            throw new PlayerNotFoundException();
        }

        Game game = gameCreator.createGame(players);
        log.trace("Saving game " + game);
        changePlayerPosition(game.getTurnOf(), new GameChange());
        gameRepository.save(game);
        return gameConverter.toDto(game);
    }


    public GameDto getGame(UUID gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow(() ->
                new GameNotFoundException(gameId));
        log.trace("Get game " + game);
        return gameConverter.toDto(game);
    }

    public GameChange finishStep(UUID gameId, UUID playerId) {
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new GameNotFoundException(gameId));
        if (!Objects.equals(game.getTurnOf().getId(), playerId)) {
            throw new NotYourStepException();
        }
        log.info("%s end turn in game {%s}", game.getTurnOf().getPlayer().getNickname(), gameId);

        Integer order = (game.getTurnOf().getOrder() + 1) % game.getPlayerStates().size();
        game.setTurnOf(game.getPlayerStates().get(order));
        gameRepository.save(game);
        GameChange gameChange = new GameChange();
        UUID turnId = game.getTurnOf().getId();
        gameChange.setTurnOf(turnId);
        String nickname = game.getTurnOf().getPlayer().getNickname();
        gameChange.addChangeDescription(format("Now it's %s turn", nickname));
        return firstStep(gameChange, gameId, turnId);
    }

    private GameChange firstStep(GameChange gameChange, UUID gameId, UUID playerId) {

        PlayerState player = playerStateRepository.findById(playerId).orElseThrow(() ->
                new PlayerNotFoundException(playerId));
        changePlayerPosition(player, gameChange);
        Integer position = player.getPosition();
        CellState cell = cellStateRepository.findByGameIdAndPosition(gameId, position)
                .orElseThrow(() -> new CellNotFoundException(gameId, position));
        gameChange.addChangeDescription(format("%s moved to %s", player.getPlayer().getNickname(), cell.getName()));


        switch (cell.getType()) {
            case START:
                break;
            case FLIGHT:
                break;
            case JAIL:
                break;
            default:
                if (cell.getOwner() == null) {
                    payToOwner(player, cell, gameChange);
                }
                break;
        }
        playerStateRepository.save(player);
        Gamer gamer = playerConverter.toDto(player);
        gameChange.addGamerChange(gamer);
        return gameChange;
    }

    private void changePlayerPosition(PlayerState playerState, GameChange gameChange) {
        int step = random.nextInt(6) + random.nextInt(6) + 2;
        int position = playerState.getPosition() + step;
        if (position >= FIELD_SIZE) {
            giveSalary(playerState, gameChange);
            playerState.setPosition(position - playerState.getPosition());
        } else {
            playerState.setPosition(position);
        }
    }

    private void giveSalary(PlayerState playerState, GameChange gameChange) {
        int salary = 200;
        playerState.setMoney(playerState.getMoney() + salary);
        gameChange.addChangeDescription(format("%s get salary: M%s", playerState.getPlayer().getNickname(), salary));

    }


    private void payToOwner(PlayerState playerState, CellState street, GameChange gameChange) {
        PlayerState owner = street.getOwner();
        int money = street.getCost();
        // TODO: If not enough money - bankrupt?
        playerState.setMoney(playerState.getMoney() - money);
        owner.setMoney(owner.getMoney() + money);
        playerStateRepository.save(owner);
        Gamer gamer = playerConverter.toDto(owner);
        gameChange.addGamerChange(gamer);
        gameChange.addChangeDescription(format("%s pay to %s M%s as a rent of %s",
                playerState.getPlayer().getNickname(), owner.getPlayer().getNickname(), money, street.getName()));
    }


    public GameChange streetStep(UUID gameId, UUID playerId, UUID playerProfileId) {
        PlayerState playerState = playerStateRepository.findById(playerId).orElseThrow(() ->
                new PlayerNotFoundException(playerId));
        if (!Objects.equals(playerState.getPlayer().getId(), playerProfileId)) {
            throw new NotAllowedOpertationException();
        }
        Integer position = playerState.getPosition();
        CellState cell = cellStateRepository.findByGameIdAndPosition(gameId, position)
                .orElseThrow(() -> new CellNotFoundException(gameId, position));
        buyStreet(playerState, cell);

        GameChange gameChange = new GameChange();
        Gamer gamer = playerConverter.toDto(playerState);
        gameChange.addGamerChange(gamer);
        Street street = cellConverter.toStreet(cell);
        gameChange.setStreetChange(street);
        return gameChange;
    }



    private boolean buyStreet(PlayerState playerState, CellState street) {
        if (street.getOwner() != null) {
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
