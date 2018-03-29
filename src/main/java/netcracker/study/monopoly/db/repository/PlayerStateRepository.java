package netcracker.study.monopoly.db.repository;

import netcracker.study.monopoly.db.model.PlayerState;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlayerStateRepository extends CrudRepository<PlayerState, UUID> {

    @Query(value = "SELECT ps " +
            "FROM players_state ps " +
            "JOIN games g ON ps.game_id = g.id " +
            "JOIN players p ON ps.player_id = p.id " +
            "WHERE g.id = ?1 AND  p.id = ?2", nativeQuery = true)
    Optional<PlayerState> findByGameIdAndPlayerId(UUID gameId, UUID playerId);
}
