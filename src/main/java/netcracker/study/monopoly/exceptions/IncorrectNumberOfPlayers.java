package netcracker.study.monopoly.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncorrectNumberOfPlayers extends RuntimeException {
    public IncorrectNumberOfPlayers(int size) {
        super("Expected from 2 to 4 players, but have " + size);
    }
}
