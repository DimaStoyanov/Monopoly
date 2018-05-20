package netcracker.study.monopoly.models.repositories;

import netcracker.study.monopoly.models.entities.CellState;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface CellStateRepository extends CrudRepository<CellState, UUID> {
    @Query(value = "select * from cells_state " +
            "where field_id = ?1 AND position = ?2", nativeQuery = true)
    Optional<CellState> findByGameIdAndPosition(UUID gameId, Integer position);

    @Query(value = "select * from cells_state " +
            "where field_id = ?1 AND position = ?2 for update", nativeQuery = true)
    Optional<CellState> findByGameIdAAndPositionWithLock(UUID gameId, Integer position);

}
