package netcracker.study.monopoly.converters;

import netcracker.study.monopoly.controller.dto.GameDto;
import netcracker.study.monopoly.controller.dto.Gamer;
import netcracker.study.monopoly.controller.dto.cells.Flight;
import netcracker.study.monopoly.controller.dto.cells.Jail;
import netcracker.study.monopoly.controller.dto.cells.Start;
import netcracker.study.monopoly.controller.dto.cells.Street;
import netcracker.study.monopoly.models.entities.CellState;
import netcracker.study.monopoly.models.entities.Game;
import netcracker.study.monopoly.models.entities.PlayerState;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import java.util.stream.Collectors;

/**
 * Binds entities from a database with data transfer objects
 */
@Mapper(componentModel = "spring")
public interface DbDtoConverter {

    @Mapping(source = "player.nickname", target = "name")
    Gamer playerToDto(PlayerState ps);


    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "order", ignore = true),
            @Mapping(target = "score", ignore = true),
            @Mapping(target = "player", ignore = true),
            @Mapping(target = "game", ignore = true)
    })
    PlayerState gamerToDb(Gamer updated, @MappingTarget PlayerState old);

    default CellState streetToDb(Street updated, CellState old, PlayerState owner) {
        old.setCost(updated.getCost());
        old.setOwner(owner);
        return old;
    }

    @Mapping(source = "owner.player.nickname", target = "owner.name")
    Street streetToDto(CellState cell);

    Flight flightToDto(CellState cell);

    Jail jailToDto(CellState cell);

    Start startToDto(CellState cell);

    /**
     * This method only need in start of the game, or if user refresh the page
     * So, avoid to send  whole {@link GameDto}
     */
    @Mapping(source = "playerStates", target = "players")
    default GameDto gameToDto(Game game) {
        GameDto gameDto = new GameDto();
        gameDto.setTurnOf(playerToDto(game.getTurnOf()));
        gameDto.setPlayers(game.getPlayerStates().stream()
                .map(this::playerToDto)
                .collect(Collectors.toList()));
        gameDto.setField(game.getField().stream()
                .map(c -> {
                    switch (c.getType()) {
                        case START:
                            return startToDto(c);
                        case JAIL:
                            return jailToDto(c);
                        case FLIGHT:
                            return flightToDto(c);
                        default:
                            return streetToDto(c);
                    }
                }).collect(Collectors.toList()));
        return gameDto;
    }
}
