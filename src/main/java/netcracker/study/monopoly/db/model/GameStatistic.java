package netcracker.study.monopoly.db.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
@Getter
public class GameStatistic extends AbstractIdentifiableObject implements Serializable {

    @Column(updatable = false)
    @NonNull
    private Integer durationMinutes;

    @Temporal(TemporalType.DATE)
    @NonNull
    private Date startedAt;

    @ManyToOne(optional = false)
    @JoinColumn
    @NonNull
    private Player winner;


    @OneToMany(mappedBy = "game")
    private Set<Score> scores;

    @PrePersist
    private void update() {
        winner.getStat().incrementTotalWins();
    }


}
