package netcracker.study.monopoly.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import java.io.Serializable;

@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@ToString(of = "score")
@Getter
public class Score extends AbstractIdentifiableObject implements Serializable {

    @ManyToOne(optional = false)
    @JoinColumn
    @NonNull
    @JsonIgnore
    private GameStatistic game;

    @ManyToOne(optional = false)
    @JoinColumn
    @NonNull
    @JsonIgnore
    private Player player;

    @NonNull
    private Integer score;


    @PrePersist
    private void updatePlayerStat() {
        player.getStat().incrementTotalGames();
        player.getStat().addTotalScore(score);
    }

}
