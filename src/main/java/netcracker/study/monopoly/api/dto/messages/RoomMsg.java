package netcracker.study.monopoly.api.dto.messages;

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
    private UUID playerId;

    private Date sendAt;
    private UUID gameId;
    private String content;
    private String avatarUrl;

    public RoomMsg(Type type, String nickname, UUID playerId) {
        this.type = type;
        this.nickname = nickname;
        this.playerId = playerId;
        sendAt = new Date();
    }

    public enum Type {
        JOIN, LEAVE, START, CHAT, KICK
    }
}
