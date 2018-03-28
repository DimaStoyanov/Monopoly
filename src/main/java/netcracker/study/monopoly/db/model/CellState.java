package netcracker.study.monopoly.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@ToString(exclude = "owner")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "cells_state")
public class CellState extends AbstractIdentifiableObject implements Serializable {

    @Setter
    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private PlayerState owner;

    @NonNull
    private Integer position;

    @Setter
    private Integer cost;

    @Setter
    @NonNull
    private String name;

    @NonNull
    @Enumerated(EnumType.STRING)
    private CellType type;

    public CellState(Integer position, String name, CellType type) {
        this.position = position;
        this.name = name;
        this.type = type;
    }

    public enum CellType {
        STREET, START, FLIGHT, JAIL
    }
}

