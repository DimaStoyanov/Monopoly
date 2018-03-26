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
    @OrderBy(value = "order")
    private List<PlayerState> playerStates;

    @ManyToOne(optional = false)
    @NonNull
    @JsonIgnore
    @Setter
    private Player turnOf;

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn
    @NonNull
    @OrderBy(value = "position")
    private List<StreetState> field;

    private boolean finished = false;

    private int durationMinutes;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date startedAt;

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private Player winner;

    public Game(List<PlayerState> playerStates, Player turnOf, List<StreetState> field) {
        this.playerStates = playerStates;
        this.turnOf = turnOf;
        this.field = field;
        this.startedAt = new Date();
        startedAt = new Date();
        playerStates.forEach(p -> p.setGame(this));
    }

    /**
     * Need to call when game is finished.
     * Set finished to true, set winner and duration of game
     * Also update statistics of players
     *
     * @param winner          - {@link Player}, that won this game
     * @param durationMinutes - how many minutes was the game
     * @throws IllegalStateException - repeated calls of this function
     */
    public void finish(Player winner, int durationMinutes) {
        if (finished) {
            throw new IllegalStateException("Game already finished");
        }

        finished = true;
        this.winner = winner;
        this.durationMinutes = durationMinutes;

        winner.getStat().incrementTotalWins();
        playerStates.forEach(p -> {
            p.getPlayer().getStat().incrementTotalGames();
            p.getPlayer().getStat().addTotalScore(p.getScore());
        });
    }



}
