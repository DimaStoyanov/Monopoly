package netcracker.study.monopoly.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BuyStreetException extends RuntimeException {
    public BuyStreetException(String message) {
        super(message);
    }
}
