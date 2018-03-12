package netcracker.study.monopoly.db.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "games")
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
public class Game {
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
}
