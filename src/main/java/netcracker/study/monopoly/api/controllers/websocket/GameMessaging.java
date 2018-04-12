package netcracker.study.monopoly.api.controllers.websocket;

import lombok.extern.log4j.Log4j2;
import netcracker.study.monopoly.api.dto.GameMsg;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static netcracker.study.monopoly.api.controllers.websocket.WebSocketEventListener.LEAVE_MSG_KEY;


@Controller
@Log4j2
public class GameMessaging {

    private final SimpMessagingTemplate messagingTemplate;

    public GameMessaging(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/games/{gameId}")
    public void processMsg(@Payload GameMsg msg, @DestinationVariable UUID gameId,
                           StompHeaderAccessor headerAccessor) {
        log.info(msg);
        switch (msg.getType()) {
            case JOIN:
                Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
                GameMsg leaveMsg = new GameMsg();
                leaveMsg.setType(GameMsg.Type.LEAVE);
                leaveMsg.setIdFrom(msg.getIdFrom());
                Runnable sendMsg = () -> this.processMsg(leaveMsg, gameId, headerAccessor);
                sessionAttributes.put(LEAVE_MSG_KEY, sendMsg);
                break;
            case LEAVE:
                msg.setSendAt(new Date());
                break;
        }
        messagingTemplate.convertAndSend("/topic/games/" + gameId, msg);
    }
}
