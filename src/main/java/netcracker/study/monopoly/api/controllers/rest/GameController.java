package netcracker.study.monopoly.api.controllers.rest;

import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j2;
import netcracker.study.monopoly.api.dto.GameMsg;
import netcracker.study.monopoly.api.dto.game.GameChange;
import netcracker.study.monopoly.api.dto.game.GameDto;
import netcracker.study.monopoly.exceptions.NotAllowedOperationException;
import netcracker.study.monopoly.managers.GameManager;
import netcracker.study.monopoly.managers.SellOfferManager;
import netcracker.study.monopoly.managers.SellOfferManager.Offer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.UUID;

import static java.lang.String.format;
import static netcracker.study.monopoly.api.controllers.rest.PlayerController.GAME_ID_KEY;
import static netcracker.study.monopoly.api.controllers.rest.PlayerController.PLAYER_STATE_ID_KEY;
import static netcracker.study.monopoly.api.dto.GameMsg.Type.*;
import static netcracker.study.monopoly.models.entities.Game.GameState.FINISHED;

@RestController
@RequestMapping("/api/v1")
@Api
@Log4j2
public class GameController {

    private final GameManager gameManager;
    public final static String TOPIC_PREFIX = "/topic/games/";
    private final SellOfferManager sellOfferManager;

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public GameController(GameManager gameManager, SellOfferManager sellOfferManager, SimpMessagingTemplate messagingTemplate) {
        this.gameManager = gameManager;
        this.sellOfferManager = sellOfferManager;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/game.get")
    public GameDto getGame(HttpSession session) {
        UUID gameId = (UUID) session.getAttribute(GAME_ID_KEY);
        log.debug("Request get game with id " + gameId);
        return gameManager.getGame(gameId);
    }

    @PutMapping("/game.start")
    public void startGame(HttpSession session) {
        UUID gameId = (UUID) session.getAttribute(GAME_ID_KEY);
        UUID playerId = (UUID) session.getAttribute(PLAYER_STATE_ID_KEY);

        GameChange gameChange = gameManager.startGame(gameId, playerId);
        log.info(format("Start game {%s}", gameId));

        GameMsg gameMsg = getGameChangeMsg(gameChange, playerId);
        messagingTemplate.convertAndSend(TOPIC_PREFIX + gameId, gameMsg);
    }

    @PutMapping("/street.buy")
    public void buyStreet(HttpSession session) {
        UUID gameId = (UUID) session.getAttribute(GAME_ID_KEY);
        UUID playerId = (UUID) session.getAttribute(PLAYER_STATE_ID_KEY);

        GameChange gameChange = gameManager.streetStep(gameId, playerId);
        log.info(gameChange.getChangeDescriptions());

        GameMsg msg = getGameChangeMsg(gameChange, playerId);
        messagingTemplate.convertAndSend(TOPIC_PREFIX + gameId, msg);
    }

    @PutMapping("/step.finish")
    public void finishStep(HttpSession session) {
        UUID gameId = (UUID) session.getAttribute(GAME_ID_KEY);
        UUID playerId = (UUID) session.getAttribute(PLAYER_STATE_ID_KEY);

        GameChange gameChange = gameManager.finishStep(gameId, playerId);
        log.info(gameChange.getChangeDescriptions());

        GameMsg msg = getGameChangeMsg(gameChange, playerId);
        if (gameChange.getCurrentState() == FINISHED) {
            msg.setType(FINISH);
            session.setAttribute(GAME_ID_KEY, null);
        }
        messagingTemplate.convertAndSend(TOPIC_PREFIX + gameId, msg);
    }

    @PutMapping("/street.sell-offer.send")
    public Integer sendSellOffer(HttpSession session, @RequestParam(name = "buyer") UUID buyerId,
                                 @RequestParam(name = "cost") Integer cost) {
        UUID gameId = (UUID) session.getAttribute(GAME_ID_KEY);
        UUID sellerId = (UUID) session.getAttribute(PLAYER_STATE_ID_KEY);

        String offerDescription = gameManager.validateOffer(gameId, sellerId, buyerId, cost);
        int offerRqId = sellOfferManager.createOffer(sellerId, buyerId, cost);

        GameMsg msg = new GameMsg();
        msg.setType(OFFER);
        msg.setIdFrom(sellerId);
        msg.setSendAt(new Date());
        msg.setOfferRqId(offerRqId);
        msg.setContent(offerDescription);
        messagingTemplate.convertAndSend(TOPIC_PREFIX + gameId, msg);

        return offerRqId;
    }

    @PutMapping("/street.sell-offer.accept")
    public void acceptOffer(HttpSession session, @RequestParam(name = "rqId") Integer rqId) {
        UUID gameId = (UUID) session.getAttribute(GAME_ID_KEY);
        UUID buyerId = (UUID) session.getAttribute(PLAYER_STATE_ID_KEY);


        Offer offer = sellOfferManager.getOffer(rqId);
        if (buyerId != offer.getBuyerId()) {
            throw new NotAllowedOperationException();
        }

        GameChange gameChange = gameManager.sellStreet(gameId, offer.getSellerId(), offer.getBuyerId(), offer.getCost());

        GameMsg gameChangeMsg = getGameChangeMsg(gameChange, buyerId);
        messagingTemplate.convertAndSend(TOPIC_PREFIX + gameId, gameChangeMsg);
    }

    @PutMapping("/street.sell-offer.decline")
    public void declineOffer(HttpSession session, @RequestParam(name = "rqId") Integer rqId,
                             @RequestParam(name = "comment", required = false) String comment) {
        UUID gameId = (UUID) session.getAttribute(GAME_ID_KEY);
        UUID buyerId = (UUID) session.getAttribute(PLAYER_STATE_ID_KEY);


        sellOfferManager.removeOffer(rqId, buyerId);

        GameMsg msg = new GameMsg();
        msg.setType(DECLINE_OFFER);
        msg.setIdFrom(buyerId);
        msg.setOfferRqId(rqId);
        msg.setContent(comment);
        msg.setSendAt(new Date());
        messagingTemplate.convertAndSend(TOPIC_PREFIX + gameId, msg);
    }

    private GameMsg getGameChangeMsg(GameChange gameChange, UUID idFrom) {
        GameMsg msg = new GameMsg();
        msg.setType(CHANGE);
        msg.setSendAt(new Date());
        msg.setIdFrom(idFrom);
        msg.setGameChange(gameChange);
        return msg;
    }

}
