package netcracker.study.monopoly.db.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.util.UUID;

@Entity
@ToString(exclude = {"id", "player"})
@NoArgsConstructor
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class PlayerStat implements Serializable {


    @Id
    @GeneratedValue(generator = "custom-uuid")
    @GenericGenerator(name = "custom-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Getter
    @NonNull
    private Integer totalScore;

    @Getter
    @NonNull
    private Integer totalWins;

    @Getter
    @NonNull
    private Integer totalGames;

    @Getter
    @OneToOne(mappedBy = "stat", optional = false)
    @NonNull
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
