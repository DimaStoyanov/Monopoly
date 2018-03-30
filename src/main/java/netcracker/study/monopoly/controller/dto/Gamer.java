package netcracker.study.monopoly.controller.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class Gamer {
    private UUID id;

    private Integer order;

    private Integer position = 0;

    private String name;

    private Integer money;

    private Boolean canGo = true;

}
