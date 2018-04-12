package netcracker.study.monopoly.api.controllers.websocket;

import lombok.extern.log4j.Log4j2;
import netcracker.study.monopoly.api.dto.OnlineStatusMsg;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static netcracker.study.monopoly.api.controllers.websocket.WebSocketEventListener.LEAVE_MSG_KEY;
import static netcracker.study.monopoly.api.dto.OnlineStatusMsg.Place.GAME;
import static netcracker.study.monopoly.api.dto.OnlineStatusMsg.Place.ROOM;
import static netcracker.study.monopoly.api.dto.OnlineStatusMsg.Status.OFFLINE;
import static netcracker.study.monopoly.api.dto.OnlineStatusMsg.Status.ONLINE;

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
    public void getOnlineStatusMessage(@Payload OnlineStatusMsg msg,
                                       StompHeaderAccessor headerAccessor) {
        UUID playerId = msg.getPlayerId();
        Set<UUID> onlinePlayers = msg.getPlace() == GAME ? inGamesPlayers :
                msg.getPlace() == ROOM ? inRoomsPlayers : inSitePlayers;

        log.info(msg);

        if (msg.getStatus() == ONLINE) {
            onlinePlayers.add(playerId);

            OnlineStatusMsg offlineMsg = new OnlineStatusMsg();
            offlineMsg.setPlace(msg.getPlace());
            offlineMsg.setStatus(OFFLINE);
            offlineMsg.setPlayerId(playerId);
            Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
            Runnable sendMsg = () -> this.getOnlineStatusMessage(
                    offlineMsg, headerAccessor);
            sessionAttributes.put(LEAVE_MSG_KEY, sendMsg);

        } else {

            onlinePlayers.remove(playerId);
        }
    }

    public String getPlayerStatus(UUID playerId) {
        return inGamesPlayers.contains(playerId) ? "In game" :
                inRoomsPlayers.contains(playerId) ? "In room" :
                        inSitePlayers.contains(playerId) ? "Online" :
                                "Offline";
    }
}
