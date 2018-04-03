package netcracker.study.monopoly.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CellNotFoundException extends RuntimeException {
    public CellNotFoundException(UUID gameId, Integer position) {
        super(String.format("Cell with game id = %s and position = %s not found", gameId, position));
    }

    public CellNotFoundException(String msg) {
        super(msg);
    }
}
