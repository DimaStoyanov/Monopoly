package netcracker.study.monopoly.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PlayerNotFoundException extends RuntimeException {

    public PlayerNotFoundException() {
        super("Player/s not found");
    }

    public PlayerNotFoundException(UUID id) {
        super(String.format("Player with id %s not found", id));
    }
}
