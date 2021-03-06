package netcracker.study.monopoly.api.controllers.websocket;

import lombok.extern.log4j.Log4j2;
import netcracker.study.monopoly.api.dto.messages.GameMsg;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static netcracker.study.monopoly.api.controllers.rest.GameController.TOPIC_PREFIX;
import static netcracker.study.monopoly.api.controllers.websocket.WebSocketEventListener.LEAVE_MSG_KEY;
import static netcracker.study.monopoly.api.dto.messages.OnlineStatusMsg.Place.GAME;
import static netcracker.study.monopoly.api.dto.messages.OnlineStatusMsg.Status.OFFLINE;
import static netcracker.study.monopoly.api.dto.messages.OnlineStatusMsg.Status.ONLINE;

@Controller
@Log4j2
public class GameMessaging {

    private final SimpMessagingTemplate messagingTemplate;
    private final PlayersTracking playersTracking;


    public GameMessaging(SimpMessagingTemplate messagingTemplate, PlayersTracking playersTracking) {
        this.messagingTemplate = messagingTemplate;
        this.playersTracking = playersTracking;
    }

    @MessageMapping("/games/{gameId}")
    private void processMsg(@Payload GameMsg msg, @DestinationVariable UUID gameId,
                            SimpMessageHeaderAccessor headerAccessor) {
        log.info(msg);
        if (msg.getType() == GameMsg.Type.JOIN) {
            Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
            playersTracking.setPlayerStatus(msg.getProfileId(), GAME, ONLINE, headerAccessor);

            GameMsg leaveMsg = new GameMsg();
            leaveMsg.setType(GameMsg.Type.LEAVE);
            leaveMsg.setIdFrom(msg.getIdFrom());
            leaveMsg.setProfileId(msg.getProfileId());
            Runnable sendMsg = () -> this.processMsg(leaveMsg, gameId, headerAccessor);
            sessionAttributes.put(LEAVE_MSG_KEY, sendMsg);

        } else if (msg.getType() == GameMsg.Type.LEAVE) {
            playersTracking.setPlayerStatus(msg.getProfileId(), GAME, OFFLINE, headerAccessor);
            msg.setSendAt(new Date());

        }
        messagingTemplate.convertAndSend(TOPIC_PREFIX + gameId, msg);
    }
}
