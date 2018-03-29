package netcracker.study.monopoly.exceptions;

import java.util.UUID;

public class EntryNotFoundException extends Exception {
    public EntryNotFoundException(UUID id) {
        super(String.format("Entity with id = %s not found", id));
    }

    public EntryNotFoundException(String msg) {
        super(msg);
    }
}
