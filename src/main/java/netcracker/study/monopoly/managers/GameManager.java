package netcracker.study.monopoly.managers;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import netcracker.study.monopoly.api.dto.Offer;
import netcracker.study.monopoly.api.dto.game.GameChange;
import netcracker.study.monopoly.api.dto.game.GameDto;
import netcracker.study.monopoly.api.dto.game.Gamer;
import netcracker.study.monopoly.api.dto.game.cells.Street;
import netcracker.study.monopoly.converters.CellConverter;
import netcracker.study.monopoly.converters.GameConverter;
import netcracker.study.monopoly.converters.PlayerConverter;
import netcracker.study.monopoly.exceptions.*;
import netcracker.study.monopoly.managers.ai.ActiveBotManager;
import netcracker.study.monopoly.managers.ai.PassiveBotManager;
import netcracker.study.monopoly.models.GameCreator;
import netcracker.study.monopoly.models.entities.CellState;
import netcracker.study.monopoly.models.entities.Game;
import netcracker.study.monopoly.models.entities.Player;
import netcracker.study.monopoly.models.entities.Player.PlayerType;
import netcracker.study.monopoly.models.entities.PlayerState;
import netcracker.study.monopoly.models.repositories.CellStateRepository;
import netcracker.study.monopoly.models.repositories.GameRepository;
import netcracker.study.monopoly.models.repositories.PlayerRepository;
import netcracker.study.monopoly.models.repositories.PlayerStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static netcracker.study.monopoly.models.entities.CellState.CellType.STREET;
import static netcracker.study.monopoly.models.entities.Game.GameState.*;
import static netcracker.study.monopoly.models.entities.Player.PlayerType.*;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Service
@Log4j2
@Transactional
public class GameManager {

    private static final int FIELD_SIZE = 23;
    private static final int BOT_DELAY = 5;
    private final Random random = new Random();
    private final ScheduledExecutorService scheduledExecutorService =
            new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());

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
    @Autowired
    private PassiveBotManager passiveBotManager;
    @Autowired
    private ActiveBotManager activeBotManager;


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

    // TODO playerId
    public GameChange startGame(UUID gameId, UUID playerId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));
        return startGame(game);
    }

    private GameChange startGame(Game game) {
        if (game.getCurrentState() != NOT_STARTED) {
            throw new IllegalStateException("Game already started");
        }

        GameChange gameChange = new GameChange();
        changePlayerPosition(game.getTurnOf(), gameChange);
        updateGameState(game);
        Gamer dtoPlayer = playerConverter.toDto(game.getTurnOf());
        gameChange.setGamersChange(Collections.singletonList(dtoPlayer));
        gameChange.setCurrentState(game.getCurrentState());
        gameChange.addChangeDescription(format("%s move to %s", dtoPlayer.getName(), game.getField().get(dtoPlayer.getPosition()).getName()));
        gameRepository.save(game);

        PlayerType playerType = game.getTurnOf().getCurrentType();
        if (playerType == PASSIVE_BOT) {
            scheduledExecutorService.schedule(() -> passiveBotManager.makeStep(game), BOT_DELAY, TimeUnit.SECONDS);
        } else if (playerType == ACTIVE_BOT) {
            scheduledExecutorService.schedule(() -> activeBotManager.makeStep(game), BOT_DELAY, TimeUnit.SECONDS);
        }

        return gameChange;
    }

    public GameDto getGame(UUID gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow(() ->
                new GameNotFoundException(gameId));
        log.trace("Get game " + game);

        if (game.getCurrentState() == NOT_STARTED && game.getTurnOf().getCurrentType() != PLAYER) {
            startGame(game);
        }

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


    private GameChange firstStep(GameChange gameChange, Game game, PlayerState player) {
        changePlayerPosition(player, gameChange);
        updateGameState(game);

        Integer position = player.getPosition();
        CellState cell = cellStateRepository.findByGameIdAndPosition(game.getId(), position)
                .orElseThrow(() -> new CellNotFoundException(game.getId(), position));
        gameChange.addChangeDescription(format("%s moved to %s", player.getPlayer().getNickname(), cell.getName()));


        if (cell.getType() == CellState.CellType.STREET) {
            if (cell.getOwner() != null) {
                if (Objects.equals(cell.getOwner().getId(), player.getId())) {
                    game.setCurrentState(CAN_ONLY_SELL);
                } else {
                    log.info(format("%s need to pay owner", player.getPlayer().getNickname()));
                    if (!payToOwner(player, cell, gameChange)) {
                        game.setCurrentState(NEED_TO_PAY_OWNER);
                        if (!checkHasOwns(game, player)) {
                            // TODO maybe add gameChange parameter to method
                            log.info(format("%s will become bankrupt", player.getPlayer().getNickname()));
                            //TODO
                            GameChange bankruptGameChange = finishStep(game, player);
                            bankruptGameChange.getGamersChange().forEach(gameChange::addGamerChange);
                            gameChange.setStreetChanges(bankruptGameChange.getStreetChanges());
                            gameChange.setTurnOf(bankruptGameChange.getTurnOf());
                            bankruptGameChange.getChangeDescriptions().forEach(gameChange::addChangeDescription);
                        }
                    }

                }
            }

        }
        playerStateRepository.save(player);
        Gamer gamer = playerConverter.toDto(player);
        gameChange.addGamerChange(gamer);
        gameChange.setCurrentState(game.getCurrentState());
        log.info(gameChange.getChangeDescriptions());
        return gameChange;
    }


    private boolean checkHasOwns(Game game, PlayerState player) {
        return game.getField().stream()
                .anyMatch(c -> c.getOwner() != null && Objects.equals(c.getOwner().getId(), player.getId()));
    }

    private void changePlayerPosition(PlayerState playerState, GameChange gameChange) {
        int step = random.nextInt(6) + random.nextInt(6) + 2;
        int position = playerState.getPosition() + step;
        log.info(format("%s move: %s+%s", playerState.getPlayer().getNickname(),
                playerState.getPosition(), step));

        if (position >= FIELD_SIZE) {
            giveSalary(playerState, gameChange);
            playerState.setPosition(position % FIELD_SIZE);
        } else {
            playerState.setPosition(position);
        }
    }

    private void giveSalary(PlayerState playerState, GameChange gameChange) {
        int salary = 200;
        log.info(format("%s money: %s+%s", playerState.getPlayer().getNickname(), playerState.getMoney(), salary));
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
        log.info(format("%s money: %s-%s", playerState.getPlayer().getNickname(), playerState.getMoney(), money));
        log.info(format("%s money: %s+%s", owner.getPlayer().getNickname(), owner.getMoney(), money));
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
        gameChange.addStreetChange(street);
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


    public String validateOffer(UUID gameId, UUID sellerId, @NonNull UUID buyerId, @NonNull Integer cost,
                                @NonNull Integer position) {
        // Retrieve data
        PlayerState seller = playerStateRepository.findById(sellerId).orElseThrow(() ->
                new PlayerNotFoundException(sellerId));
        PlayerState buyer = playerStateRepository.findById(buyerId).orElseThrow(() ->
                new PlayerNotFoundException(buyerId));
        Game game = gameRepository.findById(gameId).orElseThrow(() ->
                new GameNotFoundException(gameId));
        CellState street = game.getField().get(position);

        validateSellStreet(buyer, seller, street, game, cost);
        return format("%s wants to sell you %s for M%s. Agree to buy?", seller.getPlayer().getNickname(),
                street.getName(), cost);
    }

    public void triggerOfferSent(UUID gameId, Offer offer) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        PlayerState player = playerStateRepository.findById(offer.getBuyerId())
                .orElseThrow(() -> new PlayerNotFoundException(offer.getSellerId()));


        if (player.getCurrentType() == PlayerType.PASSIVE_BOT) {
            passiveBotManager.processOffer(offer, game);
        } else if (player.getCurrentType() == PlayerType.ACTIVE_BOT) {
            activeBotManager.processOffer(offer, game);
        }
    }

    public void triggerOfferAccept(UUID gameId, Offer offer) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        PlayerState player = playerStateRepository.findById(offer.getSellerId())
                .orElseThrow(() -> new PlayerNotFoundException(offer.getSellerId()));

        if (player.getCurrentType() == PlayerType.PASSIVE_BOT) {
            passiveBotManager.triggerAcceptOffer(offer, game);

        } else if (player.getCurrentType() == PlayerType.ACTIVE_BOT) {
            activeBotManager.triggerAcceptOffer(offer, game);
        }

    }

    public void triggerOfferDeclaim(UUID gameId, Offer offer) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));
        log.info("Trigger on " + offer);
        PlayerState player = playerStateRepository.findById(offer.getSellerId())
                .orElseThrow(() -> new PlayerNotFoundException(offer.getSellerId()));
        System.out.println(player);

        if (player.getCurrentType() == PlayerType.PASSIVE_BOT) {
            passiveBotManager.triggerDeclaimedOffer(offer, game);

        } else if (player.getCurrentType() == PlayerType.ACTIVE_BOT) {
            activeBotManager.triggerDeclaimedOffer(offer, game);
        }
    }

    public GameChange sellStreet(UUID gameId, UUID sellerId, @NonNull UUID buyerId, @NonNull Integer cost,
                                 @NonNull Integer streetPosition) {
        // Retrieve data
        PlayerState seller = playerStateRepository.findById(sellerId).orElseThrow(() ->
                new PlayerNotFoundException(sellerId));
        PlayerState buyer = playerStateRepository.findById(buyerId).orElseThrow(() ->
                new PlayerNotFoundException(sellerId));
        Game game = gameRepository.findById(gameId).orElseThrow(() ->
                new GameNotFoundException(gameId));
        CellState street = cellStateRepository.findByGameIdAAndPositionWithLock(gameId, streetPosition)
                .orElseThrow(() -> new CellNotFoundException(gameId, streetPosition));
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
        // validation that both players play in same game
        if (!Objects.equals(buyer.getGame().getId(), game.getId())) {
            throw new NotAllowedOperationException("Buyers and seller should be in the same game");
        }
        if (street.getOwner() == null) {
            throw new IllegalStateException("Street doesn't have owner");
        }
        // validation that seller owns street
        if (!Objects.equals(street.getOwner().getId(), seller.getId())) {
            throw new NotAllowedOperationException("Seller should own street");
        }
        // validation that seller is active player now
        if (!Objects.equals(game.getTurnOf().getId(), seller.getId())) {
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
        gameChange.addStreetChange(cellConverter.toStreet(street));
        gameChange.setTurnOf(game.getTurnOf().getId());
        gameChange.setCurrentState(game.getCurrentState());
        gameChange.addChangeDescription(format("%s sell %s to %s for M%s", seller.getPlayer().getNickname(),
                street.getName(), buyer.getPlayer().getNickname(), street.getCost()));
        log.info(gameChange.getChangeDescriptions());
        return gameChange;
    }


    public GameChange finishStep(UUID gameId, UUID playerId) {
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new GameNotFoundException(gameId));
        PlayerState turnOf = playerStateRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
        return finishStep(game, turnOf);
    }

    private GameChange finishStep(Game game, PlayerState turnOf) {
        if (!Objects.equals(game.getTurnOf().getId(), turnOf.getId())) {
            throw new NotYourStepException();
        }
        String nickname = turnOf.getPlayer().getNickname();
        log.info(format("%s end turn in game {%s}", nickname, game.getId()));
        GameChange gameChange = new GameChange();

        if (game.getCurrentState() == NEED_TO_PAY_OWNER) {
            if (!payToOwner(turnOf, game.getField().get(turnOf.getPosition()), gameChange)) {
                log.info(format("%s is a bankrupt", turnOf.getPlayer().getNickname()));
                turnOf.setIsBankrupt(true);
                removeAllOwns(game, turnOf, gameChange);
                turnOf.setScore(0);
                gameChange.addGamerChange(playerConverter.toDto(turnOf));
                gameChange.addChangeDescription(format("%s is a bankrupt now", nickname));
                log.debug(format("%s didn't pay for rent ant became a bankrupt", nickname));
            }
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
        firstStep(gameChange, game, turnOf);

        PlayerType currentType = game.getTurnOf().getCurrentType();
        if (currentType == PASSIVE_BOT) {
            scheduledExecutorService.schedule(() -> passiveBotManager.makeStep(game), BOT_DELAY, TimeUnit.SECONDS);
        } else if (currentType == ACTIVE_BOT) {
            scheduledExecutorService.schedule(() -> activeBotManager.makeStep(game), BOT_DELAY, TimeUnit.SECONDS);
        }

        return gameChange;
    }


    private void removeAllOwns(Game game, PlayerState player, GameChange gameChange) {
        game.getField().stream()
                .filter(c -> c.getOwner() != null && Objects.equals(c.getOwner().getId(), player.getId()))
                .forEach(c -> {
                    c.setOwner(null);
                    gameChange.addStreetChange(cellConverter.toStreet(c));
                });
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
                new IllegalStateException("Game has no players"));
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
