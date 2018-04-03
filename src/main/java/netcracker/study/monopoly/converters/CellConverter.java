package netcracker.study.monopoly.converters;

import netcracker.study.monopoly.api.dto.game.cells.Flight;
import netcracker.study.monopoly.api.dto.game.cells.Jail;
import netcracker.study.monopoly.api.dto.game.cells.Start;
import netcracker.study.monopoly.api.dto.game.cells.Street;
import netcracker.study.monopoly.models.entities.CellState;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring")
@Service
public interface CellConverter {

    @Mapping(source = "owner.player.nickname", target = "owner.name")
    Street toStreet(CellState cell);

    Flight toFlight(CellState cell);

    Jail toJail(CellState cell);

    Start toStart(CellState cell);
}
