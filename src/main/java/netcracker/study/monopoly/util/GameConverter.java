package netcracker.study.monopoly.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import netcracker.study.monopoly.controller.dto.Gamer;
import netcracker.study.monopoly.db.model.PlayerState;

/**
 * Binds entities from a database with entities from the logic game
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GameConverter {

    public static Gamer toGamer(PlayerState playerState) {
        Gamer gamer = new Gamer(playerState.getOrder(), playerState.getPlayer().getNickname());
        gamer.setMoney(playerState.getMoney());
        gamer.setPosition(playerState.getPosition());
        gamer.setCanGo(playerState.isCanGo());
        gamer.setMoney(playerState.getMoney());
        return gamer;
    }
}
