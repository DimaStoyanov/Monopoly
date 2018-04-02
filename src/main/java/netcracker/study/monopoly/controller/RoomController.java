package netcracker.study.monopoly.controller;

import lombok.extern.log4j.Log4j2;
import netcracker.study.monopoly.controller.dto.InviteMsg;
import netcracker.study.monopoly.controller.dto.RoomMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.atomic.AtomicLong;

@Controller
@Log4j2
public class RoomController {

    private final SimpMessagingTemplate template;
    private final AtomicLong lastRoomId;

    @Autowired
    public RoomController(SimpMessagingTemplate template) {
        this.template = template;
        lastRoomId = new AtomicLong();
    }

    @MessageMapping("/invite")
    public void getInvite(@Payload InviteMsg msg) {
        log.info(msg);
        template.convertAndSend("/topic/invite/" + msg.getTo(), msg);
    }

    @PostMapping("/rooms/create")
    public @ResponseBody
    long createRoom() {
        long roomId = lastRoomId.incrementAndGet();
        log.info("Created room " + roomId);
        return roomId;
    }

    @GetMapping("/rooms/{roomId}")
    public String loadRoom(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId);
        return "room";
    }

    @MessageMapping("/rooms/{roomId}")
    @SendTo("/rooms/{roomId}")
    public RoomMsg sendToRoom(@Payload RoomMsg msg) {
        return msg;
    }
}
