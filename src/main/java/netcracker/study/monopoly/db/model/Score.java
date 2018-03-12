package netcracker.study.monopoly.db.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "game_user_score")
@RequiredArgsConstructor
@NoArgsConstructor
@ToString(of = {"scoreID", "score"})
public class Score {

    @Id
    @GeneratedValue
    @Column(name = "score_id")
    private long scoreID;


    @ManyToOne
    @JoinColumn(name = "game_id")
    @Getter
    @NonNull
    private Game game;

    @ManyToOne
    @JoinColumn(name = "player_id")
    @Getter
    @NonNull
    private Player player;

    @Getter
    @NonNull
    private Integer score;

}
