package netcracker.study.monopoly.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.SerializedName;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@ToString(exclude = "owner")
@NoArgsConstructor
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
        @SerializedName("STREET") STREET,
        @SerializedName("START") START,
        @SerializedName("FLIGHT") FLIGHT,
        @SerializedName("JAIL") JAIL
    }
}

