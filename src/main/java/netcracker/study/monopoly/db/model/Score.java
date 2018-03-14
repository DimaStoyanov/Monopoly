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


//    @PostPersist
//    void updatePlayerStat(){
//        System.out.println("On score post persist");
//        player.getStat().incrementTotalGames();
//        player.getStat().setTotalScore(player.getStat().getTotalScore() + score);
//        System.out.println(player);
//    }

}
