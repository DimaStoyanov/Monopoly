package netcracker.study.monopoly.db.repository;

import netcracker.study.monopoly.db.model.Score;
import org.springframework.data.repository.CrudRepository;

public interface ScoreRepository extends CrudRepository<Score, Long> {
}
