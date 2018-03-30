package netcracker.study.monopoly.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Entity
@Getter
@ToString(exclude = {"turnOf", "winner"})
@NoArgsConstructor
@Table(name = "games")
public class Game extends AbstractIdentifiableObject implements Serializable {

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "game")
    @NonNull
    @OrderBy("order")
    private List<PlayerState> playerStates;

    @ManyToOne(optional = false)
    @NonNull
    @JsonIgnore
    @Setter
    private PlayerState turnOf;

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn
    @NonNull
    @OrderBy("position")
    private List<CellState> field;

    @Setter
    private boolean finished = false;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date startedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Setter
    private Date finishedAt;

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    @Setter
    private Player winner;

    public Game(List<PlayerState> playerStates, List<CellState> field) {
        this.playerStates = playerStates;
        this.turnOf = playerStates.get(0);
        this.field = field;
        this.startedAt = new Date();
        startedAt = new Date();
        playerStates.forEach(p -> p.setGame(this));
    }
}
