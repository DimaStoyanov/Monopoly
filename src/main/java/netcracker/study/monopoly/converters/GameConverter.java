package netcracker.study.monopoly.converters;

import lombok.NonNull;
import netcracker.study.monopoly.api.dto.game.GameDto;
import netcracker.study.monopoly.api.dto.game.Gamer;
import netcracker.study.monopoly.models.entities.Game;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * Binds entities from a database with data transfer objects
 */
@Service
public class GameConverter {

    private final CellConverter cellConverter;
    private final PlayerConverter playerConverter;

    public GameConverter(CellConverter cellConverter, PlayerConverter playerConverter) {
        this.cellConverter = cellConverter;
        this.playerConverter = playerConverter;
    }


    /**
     * This method only need in start of the game, or if user refresh the page
     * So, avoid to send  whole {@link GameDto}
     */
    public GameDto toDto(@NonNull Game game) {
        GameDto gameDto = new GameDto();
        Gamer turnOf = playerConverter.toDto(game.getTurnOf());
        gameDto.setTurnOf(turnOf);
        gameDto.setPlayers(game.getPlayerStates().stream()
                .map(playerConverter::toDto)
                .collect(Collectors.toList()));
        gameDto.setField(game.getField().stream()
                .map(c -> {
                    switch (c.getType()) {
                        case START:
                            return cellConverter.toStart(c);
                        case JAIL:
                            return cellConverter.toJail(c);
                        case FLIGHT:
                            return cellConverter.toFlight(c);
                        default:
                            return cellConverter.toStreet(c);
                    }
                }).collect(Collectors.toList()));
        gameDto.setId(game.getId());
        return gameDto;
    }
}
