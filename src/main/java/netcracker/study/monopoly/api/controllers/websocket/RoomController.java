package netcracker.study.monopoly.api.controllers.websocket;

import lombok.extern.log4j.Log4j2;
import netcracker.study.monopoly.api.dto.PlayerInfo;
import netcracker.study.monopoly.api.dto.RoomMsg;
import netcracker.study.monopoly.api.dto.game.GameDto;
import netcracker.study.monopoly.converters.PlayerInfoConverter;
import netcracker.study.monopoly.managers.GameManager;
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

import static netcracker.study.monopoly.api.controllers.websocket.WebSocketEventListener.LEAVE_MSG_KEY;
import static netcracker.study.monopoly.api.dto.OnlineStatusMsg.Place.ROOM;
import static netcracker.study.monopoly.api.dto.OnlineStatusMsg.Status.OFFLINE;
import static netcracker.study.monopoly.api.dto.OnlineStatusMsg.Status.ONLINE;
import static netcracker.study.monopoly.api.dto.RoomMsg.Type.LEAVE;

@Controller
@Log4j2
public class RoomController {


    private final SimpMessagingTemplate template;
    private final AtomicInteger lastRoomId;
    private final PlayerRepository playerRepository;
    private final Map<Integer, Set<UUID>> usersInRooms;
    private final PlayerInfoConverter playerInfoConverter;
    private final GameManager gameManager;
    private final PlayersTracking playersTracking;

    @Autowired
    public RoomController(SimpMessagingTemplate template, PlayerRepository playerRepository,
                          PlayerInfoConverter playerInfoConverter, GameManager gameManager,
                          PlayersTracking playersTracking) {
        this.template = template;
        this.playerRepository = playerRepository;
        this.playerInfoConverter = playerInfoConverter;
        this.gameManager = gameManager;
        this.playersTracking = playersTracking;
        lastRoomId = new AtomicInteger();
        usersInRooms = new HashMap<>();
    }

    @PostMapping("/rooms/create")
    public @ResponseBody
    Integer createRoom() {
        int roomId = lastRoomId.incrementAndGet();
        log.info("Created room " + roomId);
        return roomId;
    }

    @GetMapping("/rooms/{roomId}/participants")
    public @ResponseBody
    List<PlayerInfo> inRoom(@PathVariable Integer roomId) {
        Set<UUID> uuids = usersInRooms.getOrDefault(roomId, new HashSet<>());
        Collection<Player> players = (Collection<Player>) playerRepository.findAllById(uuids);
        return playerInfoConverter.toDtoAll(players);
    }

    @GetMapping("/rooms/{roomId}/host")
    public @ResponseBody
    UUID getHost(@PathVariable Integer roomId) {
        Set<UUID> players = usersInRooms.get(roomId);
        return players == null ? null : players.iterator().next();
    }

    @PostMapping("/rooms/{roomId}/start")
    public @ResponseBody
    UUID startGame(@PathVariable Integer roomId) {
        log.info("Create game in room " + roomId);
        Set<UUID> players = usersInRooms.get(roomId);
        // TODO
//        if (players.size() < 2) {
//            throw new NotEnoughPlayersException(players.size());
//        }
        GameDto gameDto = gameManager.create(players);
        return gameDto.getId();
    }

    @MessageMapping("/rooms/{roomId}")
    private void sendToRoom(@Payload RoomMsg msg, @DestinationVariable Integer roomId,
                            SimpMessageHeaderAccessor headerAccessor) {
        log.info(msg);
        usersInRooms.computeIfAbsent(roomId, i -> new LinkedHashSet<>());
        Set<UUID> players = usersInRooms.get(roomId);
        switch (msg.getType()) {
            case JOIN:
                playersTracking.setPlayerStatus(msg.getPlayerId(), ROOM, ONLINE, headerAccessor);
                Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
                RoomMsg leaveMsg = new RoomMsg(LEAVE, msg.getNickname(), msg.getPlayerId());
                Runnable sendMsg = () -> this.sendToRoom(leaveMsg, roomId, headerAccessor);
                sessionAttributes.put(LEAVE_MSG_KEY, sendMsg);
                players.add(msg.getPlayerId());
                break;
            case LEAVE:
            case KICK:
                playersTracking.setPlayerStatus(msg.getPlayerId(), ROOM, OFFLINE, headerAccessor);
                players.remove(msg.getPlayerId());
                break;

        }

        if (players.isEmpty()) {
            usersInRooms.remove(roomId);
        }
        template.convertAndSend("/topic/rooms/" + roomId, msg);
    }
}
