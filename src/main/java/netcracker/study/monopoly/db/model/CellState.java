package netcracker.study.monopoly.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Getter
@ToString(exclude = "owner")
@NoArgsConstructor
@Table(name = "cells_state")
public class CellState extends AbstractIdentifiableObject implements Serializable {

    @Setter
    private int level = 0;

    @Setter
    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private Player owner;

    private int position;

    public CellState(int position) {
        this.position = position;
    }
}
