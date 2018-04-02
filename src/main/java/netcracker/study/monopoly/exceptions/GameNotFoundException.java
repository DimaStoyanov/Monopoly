package netcracker.study.monopoly.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GameNotFoundException extends Throwable {
    public GameNotFoundException(UUID gameId) {
        super(String.format("Game with id %s not found", gameId));
    }
}
