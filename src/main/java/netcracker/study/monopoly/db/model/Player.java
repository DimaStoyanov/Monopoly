package netcracker.study.monopoly.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private List<Player> friends = new ArrayList<>();

    @Setter
    private boolean online = false;

    @Setter
    private String avatarUrl = "https://avatars3.githubusercontent.com/u/4161866?s=200&v=4";

    public Player(String nickname) {
        this.nickname = nickname;
        this.createdAt = new Date();
        stat = new PlayerStatistics();
        createdAt = new Date();
    }

    public Player(String nickname, String avatarUrl) {
        this(nickname);
        this.avatarUrl = avatarUrl;
    }

    public void addFriend(Player player) {
        friends.add(player);
        player.getFriends().add(this);
    }

    public boolean removeFriend(Player player) {
        return friends.remove(player) && player.getFriends().remove(this);
    }


}
