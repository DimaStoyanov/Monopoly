package netcracker.study.monopoly.converters;

import netcracker.study.monopoly.api.controllers.websocket.PlayersTracking;
import netcracker.study.monopoly.api.dto.PlayerInfo;
import netcracker.study.monopoly.models.entities.Player;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PlayerInfoConverter {

    private final PlayersTracking playersTracking;

    public PlayerInfoConverter(PlayersTracking playersTracking) {
        this.playersTracking = playersTracking;
    }


    public PlayerInfo toDto(Player player) {
        PlayerInfo playerInfo = new PlayerInfo();
        String playerStatus = playersTracking.getPlayerStatus(player.getId());
        playerInfo.setStatus(playerStatus);
        playerInfo.setNickname(player.getNickname());
        playerInfo.setAvatarUrl(player.getAvatarUrl());
        playerInfo.setId(player.getId());
        return playerInfo;
    }

    public List<PlayerInfo> toDtoAll(Collection<Player> players) {
        return players.stream()
                .map(this::toDto)
                .sorted((o1, o2) -> o2.getStatus().equals("Offline") ? -1 : 1)
                .collect(Collectors.toList());
    }
}
