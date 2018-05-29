package netcracker.study.monopoly.managers.ai;

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

import static netcracker.study.monopoly.api.controllers.rest.PlayerController.GAME_ID_KEY;
import static netcracker.study.monopoly.api.controllers.rest.PlayerController.PLAYER_STATE_ID_KEY;


@Service
public class PassiveBotManager implements BotManager {

    private final Random random = new Random();
    private GameController gameController;

    @Autowired
    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public void processOffer(Offer offer, Game game) {
        PlayerState self = game.getPlayerStates().stream()
                .filter(p -> Objects.equals(p.getId(), offer.getBuyerId()))
                .collect(Collectors.toList())
                .get(0);
        HttpSession session = new FakeSession(game.getId(), self.getId());
        gameController.declineOffer(session, offer.getRqId(), "I'm too old for this shit");
    }

    public void makeStep(Game game) {
        PlayerState self = game.getTurnOf();
        HttpSession session = new FakeSession(game.getId(), self.getId());
        session.setAttribute(GAME_ID_KEY, game.getId());
        session.setAttribute(PLAYER_STATE_ID_KEY, self.getId());

        switch (game.getCurrentState()) {
            case NEED_TO_PAY_OWNER:
                makeOffers(game, self, session);
                break;
            case CAN_BUY_STREET:
            case CAN_ONLY_SELL:
                gameController.finishStep(session);
            default:
        }

    }

    @Override
    public void triggerAcceptOffer(Offer offer, Game game) {

    }

    @Override
    public void triggerDeclaimedOffer(Offer offer, Game game) {

    }

    private void makeOffers(Game game, PlayerState self, HttpSession session) {
        List<CellState> owns = findYourOwns(game, self);
        int totalOwnsCost = owns.stream().mapToInt(CellState::getCost).sum();
        int needToPay = game.getField().get(self.getPosition()).getCost();
        double costRatio = (double) needToPay / totalOwnsCost;


        if (needToPay > totalOwnsCost) {
            for (CellState own : owns) {
                int sellCost = (int) Math.floor(own.getCost() * costRatio);
                sellOwn(own, sellCost, game, self, session);
            }
        } else {
            owns.forEach(own -> sellOwn(own, own.getCost(), game, self, session));
        }
    }

    private void sellOwn(CellState own, int sellCost, Game game, PlayerState self, HttpSession session) {
        List<PlayerState> potentialBuyers = game.getPlayerStates().stream()
                .filter(p -> !Objects.equals(p.getId(), self.getId()))
                .filter(p -> p.getMoney() >= sellCost)
                .collect(Collectors.toList());
        PlayerState buyer = potentialBuyers.get(random.nextInt(potentialBuyers.size()));
        Integer offerRqId = gameController.sendSellOffer(session, buyer.getId(), sellCost, own.getPosition());
    }

    private List<CellState> findYourOwns(Game game, PlayerState self) {
        return game.getField().stream()
                .filter(c -> Objects.equals(c.getOwner().getId(), self.getId()))
                .sorted((o1, o2) -> o2.getCost() - o1.getCost())
                .collect(Collectors.toList());
    }


}
