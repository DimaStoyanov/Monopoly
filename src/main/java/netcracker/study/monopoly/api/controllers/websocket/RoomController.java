package netcracker.study.monopoly.api.controllers.websocket;

import lombok.extern.log4j.Log4j2;
import netcracker.study.monopoly.api.dto.PlayerInfo;
import netcracker.study.monopoly.api.dto.game.GameDto;
import netcracker.study.monopoly.api.dto.messages.RoomMsg;
import netcracker.study.monopoly.converters.PlayerInfoConverter;
import netcracker.study.monopoly.exceptions.IncorrectNumberOfPlayers;
import netcracker.study.monopoly.exceptions.PlayerNotFoundException;
import netcracker.study.monopoly.managers.GameManager;
import netcracker.study.monopoly.models.entities.Player;
import netcracker.study.monopoly.models.entities.Player.PlayerType;
import netcracker.study.monopoly.models.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static netcracker.study.monopoly.api.controllers.rest.AIController.bots;
import static netcracker.study.monopoly.api.controllers.websocket.WebSocketEventListener.LEAVE_MSG_KEY;
import static netcracker.study.monopoly.api.dto.messages.OnlineStatusMsg.Place.ROOM;
import static netcracker.study.monopoly.api.dto.messages.OnlineStatusMsg.Status.OFFLINE;
import static netcracker.study.monopoly.api.dto.messages.OnlineStatusMsg.Status.ONLINE;
import static netcracker.study.monopoly.api.dto.messages.RoomMsg.Type.JOIN;
import static netcracker.study.monopoly.api.dto.messages.RoomMsg.Type.LEAVE;

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

    @PostMapping("/rooms/{roomId}/add-bot")
    public @ResponseBody
    String addBot(@PathVariable Integer roomId,
                  @RequestParam(name = "type") String botType) {
        PlayerType playerType = PlayerType.valueOf(botType);
        Set<UUID> botsIds = bots.get(playerType);
        for (UUID botId : botsIds) {
            if (!usersInRooms.get(roomId).contains(botId)) {
                Player bot = playerRepository.findById(botId)
                        .orElseThrow(() -> new PlayerNotFoundException(botId));
                RoomMsg roomMsg = new RoomMsg(JOIN, bot.getNickname(), bot.getId());
                // TODO Delete this shit in client too
                roomMsg.setAvatarUrl(bot.getAvatarUrl());
                sendToRoom(roomMsg, roomId, null);
                return "Success";
            }
        }
        return "Reached the maximum number of bots in room";
    }

    @PostMapping("/rooms/{roomId}/start")
    public @ResponseBody
    UUID startGame(@PathVariable Integer roomId) {
        log.info("Create game in room " + roomId);
        Set<UUID> players = usersInRooms.get(roomId);
        if (players.size() < 2 || players.size() > 4) {
            throw new IncorrectNumberOfPlayers(players.size());
        }
        GameDto gameDto = gameManager.create(players);
        return gameDto.getId();
    }


    @MessageMapping("/rooms/{roomId}")
    private void sendToRoom(@Payload RoomMsg msg, @DestinationVariable Integer roomId,
                            SimpMessageHeaderAccessor headerAccessor) {
        log.info(msg);
        usersInRooms.computeIfAbsent(roomId, i -> new LinkedHashSet<>());
        Set<UUID> players = usersInRooms.get(roomId);
        if (msg.getType() == RoomMsg.Type.JOIN) {
            playersTracking.setPlayerStatus(msg.getPlayerId(), ROOM, ONLINE, headerAccessor);
            players.add(msg.getPlayerId());

            if (headerAccessor != null) {
                Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
                RoomMsg leaveMsg = new RoomMsg(LEAVE, msg.getNickname(), msg.getPlayerId());
                Runnable sendMsg = () -> this.sendToRoom(leaveMsg, roomId, headerAccessor);
                sessionAttributes.put(LEAVE_MSG_KEY, sendMsg);
            }


        } else if (msg.getType() == RoomMsg.Type.LEAVE || msg.getType() == RoomMsg.Type.KICK) {
            playersTracking.setPlayerStatus(msg.getPlayerId(), ROOM, OFFLINE, headerAccessor);
            players.remove(msg.getPlayerId());

        }

        if (players.isEmpty()) {
            usersInRooms.remove(roomId);
        }
        template.convertAndSend("/topic/rooms/" + roomId, msg);
    }
}
