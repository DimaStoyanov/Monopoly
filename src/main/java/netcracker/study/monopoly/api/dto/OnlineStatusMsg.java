package netcracker.study.monopoly.api.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class OnlineStatusMsg {

    private Status status;
    private Place place;
    private UUID playerId;


    public enum Status {
        ONLINE, OFFLINE
    }

    public enum Place {
        SITE, ROOM, GAME
    }
}

