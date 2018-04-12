package netcracker.study.monopoly.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import netcracker.study.monopoly.api.dto.game.GameChange;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
public class GameMsg implements Serializable {

    private String content;

    private UUID idFrom;

    private String avatarUrl;

    private GameChange gameChange;

    private Date sendAt;

    private Type type;

    public enum Type {
        JOIN, LEAVE, CHAT, CHANGE
    }
}
