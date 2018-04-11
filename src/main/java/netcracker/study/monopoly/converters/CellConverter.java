package netcracker.study.monopoly.converters;

import netcracker.study.monopoly.api.dto.game.cells.*;
import netcracker.study.monopoly.models.GameCreator;
import netcracker.study.monopoly.models.entities.CellState;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring")
@Service
public interface CellConverter {
    //  TODO unmapped warnings
    @Mapping(source = "owner.player.nickname", target = "owner.name")
    Street toStreet(CellState cell);

    Flight toFlight(CellState cell);

    Jail toJail(CellState cell);

    Start toStart(CellState cell);


    @AfterMapping
    default void setCoordinatesAndImgHref(CellState cellState, @MappingTarget Cell cell) {
        System.out.println(cellState);
        Cell initCell = GameCreator.INSTANCE.getInitCell(cell.getPosition());
        cell.setImgPath(initCell.getImgPath());
        cell.setCellCoordinates(initCell.getCellCoordinates());
        cell.setRouteCoordinates(initCell.getRouteCoordinates());
    }
}
