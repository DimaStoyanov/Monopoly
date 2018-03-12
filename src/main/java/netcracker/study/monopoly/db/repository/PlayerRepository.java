package netcracker.study.monopoly.db.repository;

import netcracker.study.monopoly.db.model.Player;
import org.springframework.data.repository.CrudRepository;

public interface PlayerRepository extends CrudRepository<Player, Long> {
    Player findByNickname(String nickname);
}
