package netcracker.study.monopoly.api.controllers.websocket;

import lombok.extern.log4j.Log4j2;
import netcracker.study.monopoly.api.dto.messages.OnlineStatusMsg;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static netcracker.study.monopoly.api.controllers.websocket.WebSocketEventListener.LEAVE_MSG_KEY;
import static netcracker.study.monopoly.api.dto.messages.OnlineStatusMsg.Place.GAME;
import static netcracker.study.monopoly.api.dto.messages.OnlineStatusMsg.Place.ROOM;
import static netcracker.study.monopoly.api.dto.messages.OnlineStatusMsg.Status.OFFLINE;
import static netcracker.study.monopoly.api.dto.messages.OnlineStatusMsg.Status.ONLINE;

@Controller
@Log4j2
public class PlayersTracking {


    private Set<UUID> inSitePlayers;
    private Set<UUID> inRoomsPlayers;
    private Set<UUID> inGamesPlayers;

    public PlayersTracking() {
        this.inSitePlayers = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.inRoomsPlayers = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.inGamesPlayers = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    @MessageMapping("/status")
    private void getOnlineStatusMessage(@Payload OnlineStatusMsg msg,
                                        SimpMessageHeaderAccessor headerAccessor) {
        UUID playerId = msg.getPlayerId();

        setPlayerStatus(playerId, msg.getPlace(), msg.getStatus(), headerAccessor);
    }

    void setPlayerStatus(UUID playerId, OnlineStatusMsg.Place place, OnlineStatusMsg.Status status,
                         SimpMessageHeaderAccessor headerAccessor) {

        Set<UUID> onlinePlayers;
        if (place == GAME) {
            onlinePlayers = inGamesPlayers;
        } else if (place == ROOM) {
            onlinePlayers = inRoomsPlayers;
        } else {
            onlinePlayers = inSitePlayers;
        }


        if (status == ONLINE) {
            onlinePlayers.add(playerId);

            if (headerAccessor != null) {
                OnlineStatusMsg offlineMsg = new OnlineStatusMsg();
                offlineMsg.setPlace(place);
                offlineMsg.setStatus(OFFLINE);
                offlineMsg.setPlayerId(playerId);
                Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
                Runnable sendMsg = () -> this.getOnlineStatusMessage(
                        offlineMsg, headerAccessor);
                sessionAttributes.put(LEAVE_MSG_KEY, sendMsg);
            }
        } else {
            onlinePlayers.remove(playerId);
        }

        log.info(String.format("Player with id {%s} in %s is now %s", playerId, place, status));
    }


    public String getPlayerStatus(UUID playerId) {
        if (inGamesPlayers.contains(playerId)) {
            return "In game";
        }
        if (inRoomsPlayers.contains(playerId)) {
            return "In room";
        }
        if (inSitePlayers.contains(playerId)) {
            return "Online";
        }
        return "Offline";
    }
}
