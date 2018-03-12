package netcracker.study.monopoly.db.repository;

import netcracker.study.monopoly.db.model.Game;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<Game, Long> {

}
