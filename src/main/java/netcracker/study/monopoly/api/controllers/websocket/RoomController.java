package netcracker.study.monopoly.api.controllers.websocket;

import lombok.extern.log4j.Log4j2;
import netcracker.study.monopoly.api.controllers.filters.PlayerTracker;
import netcracker.study.monopoly.api.dto.InviteMsg;
import netcracker.study.monopoly.api.dto.RoomMsg;
import netcracker.study.monopoly.models.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Controller
@Log4j2
public class RoomController {

    private final SimpMessagingTemplate template;
    private final AtomicLong lastRoomId;
    private final PlayerRepository playerRepository;
    private final PlayerTracker playerTracker;
    private final Map<Integer, List<UUID>> usersInRooms;


    @Autowired
    public RoomController(SimpMessagingTemplate template, PlayerRepository pr, PlayerTracker playerTracker) {
        this.template = template;
        this.playerRepository = pr;
        this.playerTracker = playerTracker;
        lastRoomId = new AtomicLong();
        usersInRooms = new HashMap<>();
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
    public String loadRoom(@PathVariable String roomId) {
        return "room";
    }

    @MessageMapping("/rooms/{roomId}")
    @SendTo("/rooms/{roomId}")
    public RoomMsg sendToRoom(@Payload RoomMsg msg) {
        return msg;
    }
}
