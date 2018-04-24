package netcracker.study.monopoly.converters;

import netcracker.study.monopoly.api.dto.game.cells.*;
import netcracker.study.monopoly.models.GameCreator;
import netcracker.study.monopoly.models.entities.CellState;
import org.mapstruct.*;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring")
@Service
public interface CellConverter {
    @Mappings({
            @Mapping(source = "owner.player.nickname", target = "owner.name"),
            @Mapping(target = "imgPath", ignore = true),
            @Mapping(target = "cellCoordinates", ignore = true),
            @Mapping(target = "routeCoordinates", ignore = true)
    })
    Street toStreet(CellState cell);

    @Mappings({
            @Mapping(target = "imgPath", ignore = true),
            @Mapping(target = "cellCoordinates", ignore = true),
            @Mapping(target = "routeCoordinates", ignore = true)
    })
    Flight toFlight(CellState cell);

    @Mappings({
            @Mapping(target = "imgPath", ignore = true),
            @Mapping(target = "cellCoordinates", ignore = true),
            @Mapping(target = "routeCoordinates", ignore = true)
    })
    Jail toJail(CellState cell);

    @Mappings({
            @Mapping(target = "imgPath", ignore = true),
            @Mapping(target = "cellCoordinates", ignore = true),
            @Mapping(target = "routeCoordinates", ignore = true)
    })
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
