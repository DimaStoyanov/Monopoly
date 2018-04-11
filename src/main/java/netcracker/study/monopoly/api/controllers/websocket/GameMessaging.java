package netcracker.study.monopoly.api.controllers.websocket;

import lombok.extern.log4j.Log4j2;
import netcracker.study.monopoly.api.dto.GameMsg;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@Log4j2
public class GameMessaging {

    private final SimpMessagingTemplate messagingTemplate;

    public GameMessaging(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/game/{gameId}")
    public void processMsg(@Payload GameMsg msg, @DestinationVariable UUID gameId,
                           StompHeaderAccessor headerAccessor) {
        log.info(msg);
        messagingTemplate.convertAndSend("/topic/games/" + gameId, msg);
    }
}
