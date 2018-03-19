package netcracker.study.monopoly.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@ToString(exclude = "winner")
@Getter
public class GameStatistic extends AbstractIdentifiableObject implements Serializable {

    @Column(updatable = false)
    @NonNull
    private Integer durationMinutes;

    @Temporal(TemporalType.TIMESTAMP)
    @NonNull
    private Date startedAt;

    @ManyToOne(optional = false)
    @JoinColumn
    @NonNull
    @JsonIgnore
    private Player winner;


    @OneToMany(mappedBy = "game")
    private Set<GamePlayerScore> scores;

    @PrePersist
    private void update() {
        winner.getStat().incrementTotalWins();
    }


}
