package netcracker.study.monopoly.db.repository;

import netcracker.study.monopoly.db.model.GamePlayerScore;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ScoreRepository extends CrudRepository<GamePlayerScore, UUID> {
}
