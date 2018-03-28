package netcracker.study.monopoly.db.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@ToString
@Getter
@EqualsAndHashCode(callSuper = true)
@Table(name = "players_statistics")
public class PlayerStatistics extends AbstractIdentifiableObject implements Serializable {

    private int totalScore = 0;

    private int totalWins = 0;

    private int totalGames = 0;

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
