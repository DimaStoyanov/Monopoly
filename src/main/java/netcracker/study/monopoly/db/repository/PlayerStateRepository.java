package netcracker.study.monopoly.db.repository;

import netcracker.study.monopoly.db.model.PlayerState;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PlayerStateRepository extends CrudRepository<PlayerState, UUID> {
}
