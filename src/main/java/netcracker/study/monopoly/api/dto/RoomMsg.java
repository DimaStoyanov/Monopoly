package netcracker.study.monopoly.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
public class RoomMsg {
    @NonNull
    private Type type;
    @NonNull
    private String nickname;
    @NonNull
    private UUID idFrom;

    private Date sendAt;
    private UUID gameId;
    private String content;
    private String avatarUrl;

    public RoomMsg(Type type, String nickname, UUID idFrom) {
        this.type = type;
        this.nickname = nickname;
        this.idFrom = idFrom;
        sendAt = new Date();
    }

    public enum Type {
        JOIN, LEAVE, START, CHAT
    }
}
