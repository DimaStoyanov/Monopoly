package netcracker.study.monopoly.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static netcracker.study.monopoly.models.entities.Player.PlayerType.PLAYER;

@Entity
@ToString(exclude = "friends")
@NoArgsConstructor
@Getter
@Table(name = "players")
public class Player extends AbstractIdentifiableObject implements Serializable {

    @Temporal(TemporalType.DATE)
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
    @OrderBy("nickname")
    private Set<Player> friends = new HashSet<>();


    @Setter
    private String avatarUrl = "https://avatars3.githubusercontent.com/u/4161866?s=200&v=4";

    @Setter
    private PlayerType playerType = PLAYER;

    public Player(String nickname) {
        this.nickname = nickname;
        this.createdAt = new Date();
        stat = new PlayerStatistics();
    }

    public void addFriend(Player player) {
        friends.add(player);
    }

    public boolean removeFriend(Player player) {
        return friends.remove(player);
    }

    public void removeAllFriends() {
        friends = new HashSet<>();
    }


    public enum PlayerType {
        PLAYER, PASSIVE_BOT
//        , ACTIVE_BOT, RANDOM_BOT
    }

}
