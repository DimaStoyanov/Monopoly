package netcracker.study.monopoly.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PayRentException extends RuntimeException {
    public PayRentException(String message) {
        super(message);
    }

    public PayRentException(Throwable cause) {
        super(cause);
    }
}
