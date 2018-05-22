package netcracker.study.monopoly.api.controllers.websocket;

import lombok.extern.log4j.Log4j2;
import netcracker.study.monopoly.api.dto.messages.InviteMsg;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Log4j2
public class InviteMessaging {
    private final SimpMessagingTemplate messagingTemplate;

    public InviteMessaging(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


    @MessageMapping("/invite")
    public void getInvite(@Payload InviteMsg msg) {
        log.trace(msg);

        messagingTemplate.convertAndSend("/topic/invite/" + msg.getTo(), msg);
    }
}
