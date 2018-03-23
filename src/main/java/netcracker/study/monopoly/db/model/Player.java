package netcracker.study.monopoly.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@ToString(exclude = "friends")
@NoArgsConstructor
@Getter
@Table(name = "players")
public class Player extends AbstractIdentifiableObject implements Serializable {

    @Temporal(TemporalType.DATE)
    @NonNull
    @Column(updatable = false)
    private Date createdAt;

    @Setter
    @NonNull
    @Column(unique = true)
    private String nickname;

    @OneToOne(cascade = {CascadeType.PERSIST}, optional = false)
    @JoinColumn
    private PlayerStatistics stat;

    @ManyToMany
    @JoinTable
    @JsonIgnore
    private List<Player> friends;

    public Player(String nickname, Date createdAt) {
        this.nickname = nickname;
        this.createdAt = createdAt;
        stat = new PlayerStatistics();
    }

    public void addFriend(Player player) {
        friends.add(player);
        player.getFriends().add(this);
    }

    public boolean removeFriend(Player player) {
        return friends.remove(player) && player.getFriends().remove(this);
    }


}
