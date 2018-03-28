package netcracker.study.monopoly.db.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@MappedSuperclass
@ToString
@EqualsAndHashCode
class AbstractIdentifiableObject {
    @Id
    @GeneratedValue(generator = "custom-uuid")
    @GenericGenerator(name = "custom-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Getter
    private UUID id;

}
