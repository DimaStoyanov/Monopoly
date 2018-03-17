package netcracker.study.monopoly.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Entity
@ToString(exclude = {"player"})
@NoArgsConstructor
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class PlayerStatistic extends AbstractIdentifiableObject implements Serializable {


    @NonNull
    private Integer totalScore;

    @NonNull
    private Integer totalWins;

    @NonNull
    private Integer totalGames;

    @OneToOne(mappedBy = "stat", optional = false, cascade = CascadeType.REFRESH)
    @NonNull
    @JsonIgnore
    private Player player;

    public void incrementTotalWins() {
        totalWins++;
    }

    public void incrementTotalGames() {
        totalGames++;
    }

    public void addTotalScore(int score) {
        totalScore += score;
    }

}
