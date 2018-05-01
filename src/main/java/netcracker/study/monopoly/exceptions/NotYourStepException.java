package netcracker.study.monopoly.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotYourStepException extends RuntimeException {
    public NotYourStepException() {
        super("It's not your turn. You can't finish step");
    }
}
