package netcracker.study.monopoly.models.repositories;

import netcracker.study.monopoly.models.entities.CellState;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface CellStateRepository extends CrudRepository<CellState, UUID> {
    @Query(value = "SELECT cell " +
            "FROM cells_state  cell " +
            "JOIN games game ON cell.field_id = game.id " +
            "WHERE game.id = ?1 AND cell.position = ?2", nativeQuery = true)
    Optional<CellState> findByGameIdAndPosition(UUID gameId, Integer position);
}
