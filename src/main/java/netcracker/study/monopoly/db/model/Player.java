package netcracker.study.monopoly.db.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@ToString(exclude = {"gamesWon"})
@NoArgsConstructor
@Getter
public class Player extends AbstractIdentifiableObject implements Serializable {

    @Temporal(TemporalType.DATE)
    @NonNull
    @Column(updatable = false)
    private Date createdAt;

    @Setter
    @NonNull
    @Column(unique = true)
    private String nickname;

    @OneToMany(mappedBy = "winner", cascade = CascadeType.REFRESH)
    private Set<GameStatistic> gamesWon;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, optional = false)
    @JoinColumn
    private PlayerStatistic stat;

    @OneToMany(mappedBy = "player", cascade = CascadeType.REFRESH)
    private Set<Score> scores;

    @ManyToOne
    @JoinColumn
    private Game currentGame;

    public Player(String nickname, Date createdAt) {
        this.nickname = nickname;
        this.createdAt = createdAt;
        stat = new PlayerStatistic(0, 0, 0, this);
    }

}
