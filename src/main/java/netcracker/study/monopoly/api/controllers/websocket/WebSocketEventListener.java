package netcracker.study.monopoly.api.controllers.websocket;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

@Component
@Log4j2
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;

    public WebSocketEventListener(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }



    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        log.debug("Disconnect socket " + event.getUser().getName());
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        Runnable sendStatusMsg = (Runnable)
                sessionAttributes.get(PlayersTracking.STATUS_MSG_KEY);
        Runnable sendRoomMsg = (Runnable) sessionAttributes.get(RoomController.LEAVE_MSG_KEY);


        if (sendStatusMsg != null) {
            sendStatusMsg.run();
        }
        if (sendRoomMsg != null) {
            sendRoomMsg.run();
        }
    }
}
