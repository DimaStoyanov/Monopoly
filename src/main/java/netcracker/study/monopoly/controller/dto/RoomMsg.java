package netcracker.study.monopoly.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class RoomMsg {
    @NonNull
    private Type type;
    @NonNull
    private String playerName;
    private Date date;
    private List<UUID> players;

    public RoomMsg(Type type, String playerName) {
        this.type = type;
        this.playerName = playerName;
        date = new Date();
    }

    public enum Type {
        JOIN, LEAVE, START
    }
}
