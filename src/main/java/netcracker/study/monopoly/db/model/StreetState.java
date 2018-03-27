package netcracker.study.monopoly.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Getter
@ToString(exclude = "owner")
@NoArgsConstructor
@Table(name = "streets_state")
public class StreetState extends AbstractIdentifiableObject implements Serializable, Cloneable {

    @Setter
    private int level = 0;

    @Setter
    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private PlayerState owner;

    private int position;

    @Setter
    private int cost;

    @Setter
    private String name;

    public StreetState(int position, int cost, String name) {
        this.position = position;
        this.cost = cost;
        this.name = name;
    }

    @Override
    public StreetState clone() throws CloneNotSupportedException {
        StreetState street = (StreetState) super.clone();
        street.setName(name);
        return street;
    }
}
