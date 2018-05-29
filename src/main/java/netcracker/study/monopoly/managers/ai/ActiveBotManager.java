package netcracker.study.monopoly.managers.ai;

import lombok.extern.log4j.Log4j2;
import netcracker.study.monopoly.api.controllers.rest.GameController;
import netcracker.study.monopoly.api.dto.Offer;
import netcracker.study.monopoly.managers.ai.util.FakeSession;
import netcracker.study.monopoly.models.entities.CellState;
import netcracker.study.monopoly.models.entities.Game;
import netcracker.study.monopoly.models.entities.PlayerState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static netcracker.study.monopoly.models.entities.Game.GameState.CAN_BUY_STREET;
import static netcracker.study.monopoly.models.entities.Game.GameState.NEED_TO_PAY_OWNER;

@Service
@Log4j2
public class ActiveBotManager implements BotManager {

    private final Random random = new Random();
    private GameController gameController;

    @Autowired
    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    @Override
    public void processOffer(Offer offer, Game game) {
        PlayerState self = game.getPlayerStates().stream()
                .filter(p -> Objects.equals(p.getId(), offer.getBuyerId()))
                .collect(Collectors.toList())
                .get(0);
        HttpSession session = new FakeSession(game.getId(), self.getId());

        gameController.acceptOffer(session, offer.getRqId());
    }

    @Override
    public void makeStep(Game game) {
        PlayerState self = game.getTurnOf();
        log.debug(format("Active bot %s making step", self.getPlayer().getNickname()));
        HttpSession session = new FakeSession(game.getId(), self.getId());
        if (game.getCurrentState() == CAN_BUY_STREET) {
            log.debug("Buying street");
            if (self.getMoney() >= game.getField().get(self.getPosition()).getCost()) {
                log.info("Trying to buy street");
                gameController.buyStreet(session);
            }
        } else if (game.getCurrentState() == NEED_TO_PAY_OWNER) {
            gameController.finishStep(session);
        }

        List<CellState> owns = game.getField().stream()
                .filter(c -> c.getOwner() != null && Objects.equals(c.getOwner().getId(), self.getId()))
                .collect(Collectors.toList());
        if (owns.isEmpty()) {
            gameController.finishStep(session);
        } else {
            log.info(format("Ty to sell owns %s", owns));
            for (CellState own : owns) {
                List<PlayerState> availableBuyers = game.getPlayerStates().stream()
                        .filter(p -> !Objects.equals(p.getId(), self.getId()))
                        .filter(p -> p.getMoney() >= own.getCost())
                        .collect(Collectors.toList());
                if (availableBuyers.isEmpty()) {
                    gameController.finishStep(session);
                } else {
                    gameController.sendSellOffer(session, availableBuyers.get(random.nextInt(availableBuyers.size())).getId(),
                            own.getCost(), own.getPosition());
                }
            }
        }

    }

    @Override
    public void triggerAcceptOffer(Offer offer, Game game) {
        HttpSession session = new FakeSession(game.getId(), game.getTurnOf().getId());
        gameController.finishStep(session);
    }

    @Override
    public void triggerDeclaimedOffer(Offer offer, Game game) {
        HttpSession session = new FakeSession(game.getId(), game.getTurnOf().getId());
        gameController.finishStep(session);
    }
}
