package netcracker.study.monopoly.db.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Entity
@ToString(exclude = {"gamesWon", "id"})
@NoArgsConstructor
public class Player implements Serializable {
    @Id
    @GeneratedValue(generator = "custom-uuid")
    @GenericGenerator(name = "custom-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Temporal(TemporalType.DATE)
    @Getter
    @NonNull
    private Date createdAt;

    @Column(unique = true)
    @Getter
    @Setter
    @NonNull
    private String nickname;

    @OneToMany(mappedBy = "winner")
    @Getter
    @NonNull
    private Set<Game> gamesWon;

    @Getter
    @OneToOne(cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn
    private PlayerStat stat;

    @OneToMany(mappedBy = "player")
    @Getter
    @NonNull
    private Set<Score> scores;

    public Player(String nickname, Date createdAt) {
        this.nickname = nickname;
        this.createdAt = createdAt;
        stat = new PlayerStat(0, 0, 0, this);
    }

}
