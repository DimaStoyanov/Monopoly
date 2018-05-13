package netcracker.study.monopoly.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class NotAllowedOperationException extends RuntimeException {
    public NotAllowedOperationException() {
        super("Not allowed");
    }

    public NotAllowedOperationException(String message) {
        super(message);
    }
}
