package netcracker.study.monopoly.db.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@ToString(of = "score")
public class Score {

    @Id
    @GeneratedValue(generator = "custom-uuid")
    @GenericGenerator(name = "custom-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;


    @ManyToOne(optional = false)
    @JoinColumn(name = "game_id")
    @Getter
    @NonNull
    private Game game;

    @ManyToOne(optional = false)
    @JoinColumn
    @Getter
    @NonNull
    private Player player;

    @Getter
    @NonNull
    private Integer score;


    @PrePersist
    void updatePlayerStat() {
        player.getStat().incrementTotalGames();
        player.getStat().addTotalScore(score);
    }

}
