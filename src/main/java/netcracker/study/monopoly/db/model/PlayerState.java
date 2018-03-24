package netcracker.study.monopoly.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@ToString(exclude = {"player", "game"})
@NoArgsConstructor
@Table(name = "players_state")
public class PlayerState extends AbstractIdentifiableObject implements Serializable {

    @Setter
    private int position = 0;

    @Setter
    private boolean canGo = true;

    @Setter
    private int score = 0;

    @Setter
    private int money;

    @Setter
    @Column(name = "order_number")
    private int order;

    @ManyToOne
    @JoinColumn
    @NonNull
    @JsonIgnore
    private Player player;

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    @Setter
    private Game game;


    public PlayerState(int money, int order, Player player) {
        this.money = money;
        this.order = order;
        this.player = player;
    }
}
