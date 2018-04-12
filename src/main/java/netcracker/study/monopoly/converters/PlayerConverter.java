package netcracker.study.monopoly.converters;

import netcracker.study.monopoly.api.dto.game.Gamer;
import netcracker.study.monopoly.models.entities.PlayerState;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring")
@Service
public interface PlayerConverter {

    @Mappings({
            @Mapping(source = "player.nickname", target = "name"),
            @Mapping(source = "player.avatarUrl", target = "avatarUrl")
    })
    Gamer toDto(PlayerState ps);
}
