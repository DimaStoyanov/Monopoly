package netcracker.study.monopoly.db.repository;

import netcracker.study.monopoly.db.model.Game;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface GameRepository extends CrudRepository<Game, UUID> {

}
