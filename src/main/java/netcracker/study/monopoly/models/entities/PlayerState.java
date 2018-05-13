package netcracker.study.monopoly.models.entities;

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
    private Integer position = 0;

    @Setter
    private Boolean canGo = true;

    @Setter
    private Boolean isBankrupt = false;

    @Setter
    private Integer score = 0;

    @Setter
    @NonNull
    private Integer money;

    @Column(name = "order_number")
    @NonNull
    private Integer order;

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

    public void addScore(@NonNull Integer score) {
        this.score += score;
    }

    public void removeScore(@NonNull Integer score) {
        this.score -= score;
    }

    public void addMoney(@NonNull Integer money) {
        this.money += money;
    }

    public void removeMoney(@NonNull Integer money) {
        this.money -= money;
    }

    public PlayerState(Integer order, Player player) {
        this.money = 2000;
        this.order = order;
        this.player = player;
    }
}
