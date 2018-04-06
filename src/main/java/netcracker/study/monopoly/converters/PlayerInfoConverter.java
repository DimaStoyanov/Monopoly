package netcracker.study.monopoly.converters;

import netcracker.study.monopoly.api.controllers.filters.PlayerTracker;
import netcracker.study.monopoly.api.dto.PlayerInfo;
import netcracker.study.monopoly.models.entities.Player;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PlayerInfoConverter {

    private final PlayerTracker playerTracker;

    public PlayerInfoConverter(PlayerTracker playerTracker) {
        this.playerTracker = playerTracker;
    }

    public PlayerInfo toDto(Player player) {
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.setOnline(playerTracker.isOnline(player.getNickname()));
        playerInfo.setNickname(player.getNickname());
        playerInfo.setAvatarUrl(player.getAvatarUrl());
        playerInfo.setId(player.getId());
        return playerInfo;
    }

    public List<PlayerInfo> toDtoAll(Collection<Player> players) {
        return players.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
