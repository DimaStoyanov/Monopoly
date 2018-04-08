package netcracker.study.monopoly.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotEnoughPlayersException extends RuntimeException {
    public NotEnoughPlayersException(int size) {
        super("Expected at least 2 players, but have " + size);
    }
}
