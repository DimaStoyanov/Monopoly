package netcracker.study.monopoly.db.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@ToString(exclude = "id")
public class Game implements Serializable {
    @Id
    @GeneratedValue(generator = "custom-uuid")
    @GenericGenerator(name = "custom-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Getter
    @NonNull
    @Column(updatable = false)
    private Integer durationMinutes;

    @Temporal(TemporalType.DATE)
    @Getter
    @NonNull
    private Date dateStarted;

    @ManyToOne(optional = false)
    @JoinColumn
    @Getter
    @NonNull
    private Player winner;

    @OneToMany(mappedBy = "game")
    @Getter
    private Set<Score> scores;

//    @PostPersist
//    private void update(){
//        System.out.println("On game post persist");
//        winner.getStat().incrementTotalWins();
//    }


}
