package netcracker.study.monopoly.api.controllers.websocket;

import lombok.extern.log4j.Log4j2;
import netcracker.study.monopoly.api.dto.InviteMsg;
import netcracker.study.monopoly.api.dto.PlayerInfo;
import netcracker.study.monopoly.api.dto.RoomMsg;
import netcracker.study.monopoly.converters.PlayerInfoConverter;
import netcracker.study.monopoly.models.entities.Player;
import netcracker.study.monopoly.models.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static netcracker.study.monopoly.api.dto.RoomMsg.Type.LEAVE;

@Controller
@Log4j2
public class RoomController {

    private final SimpMessagingTemplate template;
    private final AtomicInteger lastRoomId;
    private final PlayerRepository playerRepository;
    private final Map<Integer, Set<UUID>> usersInRooms;
    private final PlayerInfoConverter playerInfoConverter;

    @Autowired
    public RoomController(SimpMessagingTemplate template, PlayerRepository playerRepository,
                          PlayerInfoConverter playerInfoConverter) {
        this.template = template;
        this.playerRepository = playerRepository;
        this.playerInfoConverter = playerInfoConverter;
        lastRoomId = new AtomicInteger();
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

    @GetMapping("/rooms/{roomId}/participants")
    public @ResponseBody
    List<PlayerInfo> inRoom(@PathVariable Integer roomId) {
        Set<UUID> uuids = usersInRooms.getOrDefault(roomId, new HashSet<>());
        Collection<Player> players = (Collection<Player>) playerRepository.findAllById(uuids);
        return playerInfoConverter.toDtoAll(players);
    }

    @MessageMapping("/rooms/{roomId}")
    public void sendToRoom(@Payload RoomMsg msg, @DestinationVariable Integer roomId,
                           SimpMessageHeaderAccessor headerAccessor) {
        log.info(msg);
        usersInRooms.computeIfAbsent(roomId, i -> new HashSet<>());
        Set<UUID> uuids = usersInRooms.get(roomId);
        switch (msg.getType()) {
            case JOIN:
                Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
                sessionAttributes.put("destination", "/topic/rooms/" + roomId);
                RoomMsg leaveMsg = new RoomMsg(LEAVE, msg.getNickname(), msg.getPlayerId());
                sessionAttributes.put("leaveMsg", leaveMsg);
                uuids.add(msg.getPlayerId());
                break;
            case LEAVE:
                uuids.remove(msg.getPlayerId());
                break;
            case KICK:
                uuids.remove(msg.getPlayerId());
                break;

        }
        template.convertAndSend("/topic/rooms/" + roomId, msg);
    }
}
