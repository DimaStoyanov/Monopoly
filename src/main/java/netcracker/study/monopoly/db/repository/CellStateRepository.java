package netcracker.study.monopoly.db.repository;

import netcracker.study.monopoly.db.model.CellState;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface CellStateRepository extends CrudRepository<CellState, UUID> {
    @Query(value = "SELECT cell " +
            "FROM cells_state  cell " +
            "JOIN games game ON cell.field_id = game.id " +
            "JOIN players_state ps ON game.id = ps.game_id " +
            "JOIN players player ON ps.player_id = player.id " +
            "WHERE game.id = ?1 AND player.id = ?2", nativeQuery = true)
    Optional<CellState> findByGameIdAndPlayerId(UUID gameId, UUID playerId);
}
