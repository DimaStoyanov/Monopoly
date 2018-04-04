package netcracker.study.monopoly.api.controllers.websocket;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import javax.servlet.ServletContextEvent;
import java.util.Map;

@Component
@Log4j2
public class WebSocketEventListener {

    @Autowired
    SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("Received a new web socket connection " +
                event.getSource());
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        Object msg = sessionAttributes.get("leaveMsg");
        String dest = (String) sessionAttributes.get("destination");
        messagingTemplate.convertAndSend(dest, msg);
    }
}
