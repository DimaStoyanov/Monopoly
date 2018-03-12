package netcracker.study.monopoly.db.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "games")
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
public class Game implements Serializable {
    @Id
    @GeneratedValue
    @Column(name = "game_id")
    private long gameID;

    @Getter
    @NonNull
    @Column(updatable = false)
    private Integer durationMinutes;

    @Temporal(TemporalType.DATE)
    @Getter
    @NonNull
    private Date dateStarted;

    @ManyToOne
    @JoinColumn(name = "player_id")
    @Getter
    @NonNull
    private Player winner;

    @OneToMany(mappedBy = "game")
    @Getter
    private Set<Score> scores;
}
