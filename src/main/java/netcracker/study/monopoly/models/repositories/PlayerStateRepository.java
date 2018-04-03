package netcracker.study.monopoly.models.repositories;

import netcracker.study.monopoly.models.entities.PlayerState;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PlayerStateRepository extends CrudRepository<PlayerState, UUID> {
}
