package netcracker.study.monopoly.api.dto.game;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class Gamer implements Serializable {
    private UUID id;

    private Integer order;

    private Integer position = 0;

    private String name;

    private Integer money;

    private Boolean canGo = true;

    private Boolean isBankrupt = false;

    private String avatarUrl;

    private Integer score = 0;

}
