package netcracker.study.monopoly.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static netcracker.study.monopoly.models.entities.Game.GameState.NOT_STARTED;


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

    @Setter
    private GameState currentState = NOT_STARTED;

    public enum GameState {
        CAN_BUY_STREET, NEED_TO_PAY_OWNER, CAN_ONLY_SELL, NOT_STARTED, FINISHED
    }

    public Game(List<PlayerState> playerStates, List<CellState> field) {
        this.playerStates = playerStates;
        this.turnOf = playerStates.get(0);
        this.field = field;
        this.startedAt = new Date();
        startedAt = new Date();
        playerStates.forEach(p -> p.setGame(this));
    }
}
