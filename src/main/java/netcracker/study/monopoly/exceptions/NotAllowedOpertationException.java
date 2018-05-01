package netcracker.study.monopoly.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class NotAllowedOpertationException extends RuntimeException {
    public NotAllowedOpertationException() {
        super("Not allowed");
    }
}
