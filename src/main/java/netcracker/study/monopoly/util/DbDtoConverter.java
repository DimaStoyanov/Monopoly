package netcracker.study.monopoly.util;

import netcracker.study.monopoly.controller.dto.Gamer;
import netcracker.study.monopoly.controller.dto.cells.Flight;
import netcracker.study.monopoly.controller.dto.cells.Jail;
import netcracker.study.monopoly.controller.dto.cells.Start;
import netcracker.study.monopoly.controller.dto.cells.Street;
import netcracker.study.monopoly.db.model.CellState;
import netcracker.study.monopoly.db.model.PlayerState;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

/**
 * Binds entities from a database with data transfer objects
 */
@Mapper(componentModel = "spring")
public interface DbDtoConverter {

    @Mapping(source = "player.nickname", target = "name")
    Gamer playerToDto(PlayerState ps);

    @Mappings({
            @Mapping(target = "score", ignore = true),
            @Mapping(target = "player", ignore = true),
            @Mapping(target = "game", ignore = true)
    })
    PlayerState gamerToDb(Gamer updated, @MappingTarget PlayerState old);


    Street streetToDto(CellState cell);

    @Mapping(target = "type", ignore = true)
    CellState streetToDb(Street updated, @MappingTarget CellState old);

    Flight flightToDto(CellState cell);

    Jail jailToDto(CellState cell);

    Start startToDto(CellState cell);
}
