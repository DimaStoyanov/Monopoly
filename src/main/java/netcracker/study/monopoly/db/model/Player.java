package netcracker.study.monopoly.db.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "players")
@ToString(exclude = {"gamesWon"})
@NoArgsConstructor
public class Player implements Serializable {
    public Player(String nickname, Date dateCreated) {
        this.nickname = nickname;
        this.dateCreated = dateCreated;
        totalGames = totalWins = totalScore = 0;
    }

    @Id
    @GeneratedValue
    @Column(name = "player_id")
    private long playerID;

    @Column(unique = true)
    @Getter
    @Setter
    @NonNull
    private String nickname;

    @OneToMany(mappedBy = "winner")
    @Getter
    @NonNull
    private Set<Game> gamesWon;

    @Temporal(TemporalType.DATE)
    @Getter
    @NonNull
    private Date dateCreated;

    @Getter
    @Setter
    @NonNull
    private Integer totalScore;

    @Getter
    @Setter
    @NonNull
    private Integer totalWins;

    @Getter
    @Setter
    @NonNull
    private Integer totalGames;

    @OneToMany(mappedBy = "player")
    @Getter
    @NonNull
    private Set<Score> scores;

}
