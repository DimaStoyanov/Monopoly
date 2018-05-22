package netcracker.study.monopoly.api.dto.messages;

import lombok.Data;
import lombok.NoArgsConstructor;
import netcracker.study.monopoly.api.dto.game.GameChange;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
public class GameMsg implements Serializable {

    private String content;

    private UUID idFrom;

    private UUID receiverId;

    private UUID profileId;

    private GameChange gameChange;

    private Date sendAt;

    private Integer offerRqId;

    private Collection<Integer> cancelledOffersRqId;

    private Type type;

    public enum Type {
        JOIN, LEAVE, CHAT, CHANGE, FINISH, OFFER, DECLINE_OFFER
    }
}
