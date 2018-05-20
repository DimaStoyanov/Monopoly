package netcracker.study.monopoly.managers;

import lombok.NonNull;
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
import static netcracker.study.monopoly.models.entities.CellState.CellType.STREET;
import static netcracker.study.monopoly.models.entities.Game.GameState.*;

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
        gameRepository.save(game);
        return gameConverter.toDto(game);
    }

    public GameChange startGame(UUID gameId, UUID playerId) {
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new GameNotFoundException(gameId));
        if (game.getCurrentState() != NOT_STARTED || !Objects.equals(playerId, game.getTurnOf().getId())) {
            throw new NotAllowedOperationException();
        }

        GameChange gameChange = new GameChange();
        changePlayerPosition(game.getTurnOf(), gameChange);
        updateGameState(game);
        Gamer dtoPlayer = playerConverter.toDto(game.getTurnOf());
        gameChange.setGamersChange(Collections.singletonList(dtoPlayer));
        gameChange.setCurrentState(game.getCurrentState());
        gameChange.addChangeDescription(format("%s move to %s", dtoPlayer.getName(), game.getField().get(dtoPlayer.getPosition()).getName()));
        gameRepository.save(game);

        return gameChange;
    }

    public GameDto getGame(UUID gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow(() ->
                new GameNotFoundException(gameId));
        log.trace("Get game " + game);
        return gameConverter.toDto(game);
    }


    private void updateGameState(Game game) {
        PlayerState turnOf = game.getTurnOf();
        CellState currentCell = game.getField().get(turnOf.getPosition());
        if (currentCell.getType() == STREET && currentCell.getOwner() == null) {
            game.setCurrentState(CAN_BUY_STREET);
        } else {
            game.setCurrentState(CAN_ONLY_SELL);
        }
    }

    private GameChange firstStep(GameChange gameChange, UUID gameId, UUID playerId) {

        PlayerState player = playerStateRepository.findById(playerId).orElseThrow(() ->
                new PlayerNotFoundException(playerId));
        Game game = gameRepository.findById(gameId).orElseThrow(() ->
                new GameNotFoundException(gameId));
        changePlayerPosition(player, gameChange);
        updateGameState(game);

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
                if (cell.getOwner() != null) {
                    if (Objects.equals(cell.getOwner().getId(), playerId)) {
                        game.setCurrentState(CAN_ONLY_SELL);
                    } else {
                        if (!payToOwner(player, cell, gameChange)) {
                            game.setCurrentState(NEED_TO_PAY_OWNER);
                        }
                    }
                }
                break;
        }
        playerStateRepository.save(player);
        Gamer gamer = playerConverter.toDto(player);
        gameChange.addGamerChange(gamer);
        gameChange.setCurrentState(game.getCurrentState());
        log.info(gameChange.getChangeDescriptions());
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

    public GameChange payRent(UUID gameId, UUID playerId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));
        PlayerState player = playerStateRepository.findById(playerId).orElseThrow(
                () -> new PlayerNotFoundException(playerId)
        );
        if (!Objects.equals(game.getTurnOf().getId(), playerId)) {
            throw new PayRentException(new NotYourStepException());
        }

        Integer position = player.getPosition();
        CellState cellState = game.getField().get(position);
        if (game.getCurrentState() != NEED_TO_PAY_OWNER || cellState.getOwner() == null
                || cellState.getOwner().getId() == playerId) {
            throw new PayRentException("You don't need to pay owner right now");
        }
        GameChange gameChange = new GameChange();
        payToOwner(player, cellState, gameChange);
        gameChange.addGamerChange(playerConverter.toDto(player));
        return gameChange;
    }


    private boolean payToOwner(PlayerState playerState, CellState street, GameChange gameChange) {
        PlayerState owner = street.getOwner();
        int money = street.getCost();
        if (playerState.getMoney() < money) {
            return false;
        }
        playerState.setMoney(playerState.getMoney() - money);
        owner.setMoney(owner.getMoney() + money);
        playerStateRepository.save(owner);
        Gamer gamer = playerConverter.toDto(owner);
        gameChange.addGamerChange(gamer);
        gameChange.addChangeDescription(format("%s pay to %s M%s as a rent of %s",
                playerState.getPlayer().getNickname(), owner.getPlayer().getNickname(), money, street.getName()));
        return true;
    }


    public GameChange streetStep(UUID gameId, UUID playerId) {
        PlayerState playerState = playerStateRepository.findById(playerId).orElseThrow(() ->
                new PlayerNotFoundException(playerId));
        Integer position = playerState.getPosition();
        CellState cell = cellStateRepository.findByGameIdAndPosition(gameId, position)
                .orElseThrow(() -> new CellNotFoundException(gameId, position));
        Game game = gameRepository.findById(gameId).orElseThrow(() ->
                new GameNotFoundException(gameId));

        GameChange gameChange = new GameChange();
        buyStreet(playerState, cell, gameChange);
        game.setCurrentState(CAN_ONLY_SELL);

        Gamer gamer = playerConverter.toDto(playerState);
        gameChange.addGamerChange(gamer);
        Street street = cellConverter.toStreet(cell);
        gameChange.setStreetChange(street);
        return gameChange;
    }


    private void buyStreet(PlayerState playerState, CellState street, GameChange gameChange) {
        if (street.getOwner() != null) {
            throw new BuyStreetException("Can't buy street. It already has owner");
        }
        int cost = street.getCost();
        if (playerState.getMoney() < cost) {
            throw new BuyStreetException("Can't buy street. Not enough money");
        }
        playerState.setMoney(playerState.getMoney() - cost);
        street.setOwner(playerState);
        playerState.addScore(cost);
        gameChange.addChangeDescription(format("%s bought %s for M%s", playerState.getPlayer().getNickname(),
                street.getName(), cost));
        playerStateRepository.save(playerState);
        cellStateRepository.save(street);
    }

    public String validateOffer(UUID gameId, UUID sellerId, @NonNull UUID buyerId, @NonNull Integer cost) {
        // Retrieve data
        PlayerState seller = playerStateRepository.findById(sellerId).orElseThrow(() ->
                new PlayerNotFoundException(sellerId));
        PlayerState buyer = playerStateRepository.findById(buyerId).orElseThrow(() ->
                new PlayerNotFoundException(sellerId));
        Game game = gameRepository.findById(gameId).orElseThrow(() ->
                new GameNotFoundException(gameId));
        CellState street = game.getField().get(seller.getPosition());

        validateSellStreet(buyer, seller, street, game, cost);
        return format("%s wants to sell you %s for M%s. Agree to buy?", seller.getPlayer().getNickname(),
                street.getName(), cost);
    }

    public GameChange sellStreet(UUID gameId, UUID sellerId, @NonNull UUID buyerId, @NonNull Integer cost) {
        // Retrieve data
        PlayerState seller = playerStateRepository.findById(sellerId).orElseThrow(() ->
                new PlayerNotFoundException(sellerId));
        PlayerState buyer = playerStateRepository.findById(buyerId).orElseThrow(() ->
                new PlayerNotFoundException(sellerId));
        Game game = gameRepository.findById(gameId).orElseThrow(() ->
                new GameNotFoundException(gameId));
        Integer position = seller.getPosition();
        CellState street = cellStateRepository.findByGameIdAAndPositionWithLock(gameId, position)
                .orElseThrow(() -> new CellNotFoundException(gameId, position));
        // Validating
        validateSellStreet(buyer, seller, street, game, cost);
        // Business logic
        street.setOwner(buyer);
        seller.addMoney(cost);
        buyer.removeMoney(cost);
        seller.removeScore(street.getCost());
        street.setCost(cost);
        buyer.addScore(cost);
        gameRepository.save(game);
        // Build response
        return getGameChangeForSellStreet(buyer, seller, street, game);
    }

    private void validateSellStreet(PlayerState buyer, PlayerState seller, CellState street, Game game, Integer cost) {
        if (cost < 0) {
            throw new SellStreetException("Can't sell street for a negative cost");
        }
        // validation that both playersCircle play in same game
        if (!Objects.equals(buyer.getGame().getId(), game.getId())) {
            throw new NotAllowedOperationException();
        }
        // validation that seller owns street
        if (!Objects.equals(street.getOwner(), seller)) {
            throw new NotAllowedOperationException();
        }
        // validation that seller is walking player now
        if (!Objects.equals(game.getTurnOf().getId(), buyer.getId())) {
            throw new NotYourStepException();
        }
        if (buyer.getMoney() < cost) {
            throw new SellStreetException("Can't sell street. Buyer has not enough money");
        }
    }

    private GameChange getGameChangeForSellStreet(PlayerState buyer, PlayerState seller, CellState street, Game game) {
        GameChange gameChange = new GameChange();
        gameChange.addGamerChange(playerConverter.toDto(buyer));
        gameChange.addGamerChange(playerConverter.toDto(seller));
        gameChange.setStreetChange(cellConverter.toStreet(street));
        gameChange.setTurnOf(game.getTurnOf().getId());
        gameChange.setCurrentState(game.getCurrentState());
        gameChange.addChangeDescription(format("%s sell %s to %s for M%s", seller.getPlayer().getNickname(),
                street.getName(), buyer.getPlayer().getNickname(), street.getCost()));
        return gameChange;
    }

    public GameChange finishStep(UUID gameId, UUID playerId) {
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new GameNotFoundException(gameId));
        PlayerState turnOf = game.getTurnOf();
        if (!Objects.equals(turnOf.getId(), playerId)) {
            throw new NotYourStepException();
        }
        String nickname = turnOf.getPlayer().getNickname();
        log.info("%s end turn in game {%s}", nickname, gameId);
        GameChange gameChange = new GameChange();

        if (game.getCurrentState() == NEED_TO_PAY_OWNER) {
            turnOf.setIsBankrupt(true);
            gameChange.addGamerChange(playerConverter.toDto(turnOf));
            gameChange.addChangeDescription(format("%s is a bankrupt now", nickname));
            log.debug(format("%s didn't pay for rent ant became a bankrupt", nickname));
        }


        if (checkIsGameFinished(game)) {
            calculateFinalScore(game);
            PlayerState winner = findWinner(game);
            finishGame(game, winner);
            gameChange.setCurrentState(game.getCurrentState());
            game.getPlayerStates().forEach(p -> gameChange.addGamerChange(playerConverter.toDto(p)));
            gameChange.addChangeDescription(format("Game is finished. %s won!", winner.getPlayer().getNickname()));
            return gameChange;
        }

        turnOf = findNextPlayer(game);
        game.setTurnOf(turnOf);
        updateGameState(game);
        gameRepository.save(game);

        UUID turnId = turnOf.getId();
        gameChange.setTurnOf(turnId);
        nickname = game.getTurnOf().getPlayer().getNickname();
        gameChange.addChangeDescription(format("Now it's %s turn", nickname));
        return firstStep(gameChange, gameId, turnId);
    }

    private PlayerState findNextPlayer(Game game) {
        PlayerState turnOf = game.getTurnOf();
        Integer order = turnOf.getOrder();
        do {
            order = (order + 1) % game.getPlayerStates().size();
            turnOf = game.getPlayerStates().get(order);
        } while (turnOf.getIsBankrupt());
        return turnOf;
    }

    private boolean checkIsGameFinished(Game game) {
        int bankruptCount = 0;
        List<PlayerState> playerStates = game.getPlayerStates();
        for (PlayerState playerState : playerStates) {
            if (playerState.getIsBankrupt()) {
                bankruptCount++;
            }
        }

        if (playerStates.size() < 2) {
            throw new IllegalStateException("In game must be at least 2 players, but found " + playerStates.size());
        } else if (playerStates.size() < 4) {
            return bankruptCount >= 1;
        } else {
            return bankruptCount >= 2;
        }
    }

    private void calculateFinalScore(Game game) {
        for (PlayerState playerState : game.getPlayerStates()) {
            if (playerState.getIsBankrupt()) {
                playerState.setScore(0);
            } else {
                playerState.addScore(playerState.getMoney());
            }
        }
    }

    private PlayerState findWinner(Game game) {
        return game.getPlayerStates().stream().max(Comparator.comparing(PlayerState::getScore)).orElseThrow(() ->
                new IllegalStateException("Game has no playersCircle"));
    }


    private void finishGame(Game game, PlayerState winner) {
        if (game.getCurrentState() == FINISHED) {
            throw new IllegalStateException(format("Game %s has already finished", game));
        }

        game.setWinner(winner.getPlayer());
        game.setFinishedAt(new Date());
        game.getWinner().getStat().incrementTotalWins();
        game.getPlayerStates().forEach(p -> {
            p.getPlayer().getStat().incrementTotalGames();
            p.getPlayer().getStat().addTotalScore(p.getScore());
        });
        game.setCurrentState(FINISHED);
        gameRepository.save(game);
    }


}
