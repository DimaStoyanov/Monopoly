package netcracker.study.monopoly.models.repositories;

import netcracker.study.monopoly.models.entities.Player;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository extends CrudRepository<Player, UUID> {
    Optional<Player> findByNickname(String nickname);
}
