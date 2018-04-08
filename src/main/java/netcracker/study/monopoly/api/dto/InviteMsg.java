package netcracker.study.monopoly.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class InviteMsg implements Serializable {
    private String from;
    private UUID to;
    private Long roomId;
}

