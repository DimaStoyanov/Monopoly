package netcracker.study.monopoly.db;

import lombok.NonNull;
import netcracker.study.monopoly.db.model.CellState;
import netcracker.study.monopoly.db.model.Game;
import netcracker.study.monopoly.db.model.Player;
import netcracker.study.monopoly.db.model.PlayerState;
import netcracker.study.monopoly.db.repository.CellStateRepository;
import netcracker.study.monopoly.db.repository.GameRepository;
import netcracker.study.monopoly.db.repository.PlayerRepository;
import netcracker.study.monopoly.db.repository.PlayerStateRepository;
import netcracker.study.monopoly.exceptions.EntryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;


@Service
@Transactional
public class DbManager {


    private final PlayerStateRepository psr;
    private final CellStateRepository csr;
    private final GameRepository gr;
    private final PlayerRepository pr;

    @Autowired
    public DbManager(PlayerStateRepository psr, CellStateRepository csr,
                     GameRepository gr, PlayerRepository pr) {
        this.psr = psr;
        this.csr = csr;
        this.gr = gr;
        this.pr = pr;
    }

    public Game createGame(List<UUID> playerIds) throws EntryNotFoundException {
        List<Player> players = (List<Player>) pr.findAllById(playerIds);
        if (players.size() != playerIds.size()) {
            throw new EntryNotFoundException(format("Some of players with ids %s not found", playerIds));
        }
        Game game = GameCreator.INSTANCE.createGame(players);
        gr.save(game);
        return game;
    }

    public Game getGame(UUID gameId) throws EntryNotFoundException {
        return gr.findById(gameId).orElseThrow(() ->
                new EntryNotFoundException(format("Game with game id = %s not found", gameId)));
    }


    public CellState getCell(UUID gameId, @NonNull Integer position) throws EntryNotFoundException {
        return csr.findByGameIdAndPosition(gameId, position).orElseThrow(() ->
                new EntryNotFoundException(format("Cell with game id = %s 0" +
                        "in position %s not found", gameId, position)));
    }

    public PlayerState getPlayer(UUID playerStateId) throws EntryNotFoundException {
        return psr.findById(playerStateId).orElseThrow(() ->
                new EntryNotFoundException(format("PlayerState with id = %s " +
                        "not found", playerStateId)));
    }

    public UUID getTurnOf(UUID gameId) throws EntryNotFoundException {
        Game game = gr.findById(gameId).orElseThrow(() -> new EntryNotFoundException(gameId));
        return game.getTurnOf().getPlayer().getId();
    }

    public void setTurnOf(UUID playerStateId, UUID gameId) throws EntryNotFoundException {
        PlayerState ps = psr.findById(playerStateId).orElseThrow(() ->
                new EntryNotFoundException(format("PlayerState with id = %s " +
                        "not found", playerStateId)));
        Game game = gr.findById(gameId).orElseThrow(() -> new EntryNotFoundException(gameId));
        game.setTurnOf(ps);
        gr.save(game);
    }


    public void updateCell(CellState cell) throws EntryNotFoundException {
        if (!csr.findById(cell.getId()).isPresent()) {
            throw new EntryNotFoundException(cell.getId());
        }
        csr.save(cell);
    }

    public void updatePlayer(PlayerState player) throws EntryNotFoundException {
        if (!psr.findById(player.getId()).isPresent()) {
            throw new EntryNotFoundException(player.getId());
        }
        psr.save(player);
    }

    /**
     * Update both player                                                                                                                                                                                                                                                                                                                                                                                                                                                and cell in one transaction
     */
    public void updatePlayerAndCell(PlayerState player, CellState cell) throws EntryNotFoundException {
        updatePlayer(player);
        updateCell(cell);
    }


    public void finishGame(UUID gameId, UUID winnerStateId) throws EntryNotFoundException {
        Game game = gr.findById(gameId).orElseThrow(() -> new EntryNotFoundException(gameId));
        if (game.isFinished()) {
            throw new IllegalStateException(format("Game %s has already finished", game));
        }

        game.setWinner(psr.findById(winnerStateId)
                .orElseThrow(() -> new EntryNotFoundException(winnerStateId))
                .getPlayer());
        game.setFinishedAt(new Date());
        game.getWinner().getStat().incrementTotalWins();
        game.getPlayerStates().forEach(p -> {
            p.getPlayer().getStat().incrementTotalGames();
            p.getPlayer().getStat().addTotalScore(p.getScore());
        });
        game.setFinished(true);
    }

    public boolean addFriend(UUID from, UUID to) throws EntryNotFoundException {
        Player pFrom = pr.findById(from).orElseThrow(() -> new EntryNotFoundException(from));
        Player pTo = pr.findById(to).orElseThrow(() -> new EntryNotFoundException(to));
        if (pFrom.getFriends().contains(pTo)) {
            return false;
        }
        pFrom.addFriend(pTo);
        pr.save(pFrom);
        return true;
    }

    public boolean removeFriend(UUID from, UUID to) throws EntryNotFoundException {
        Player pFrom = pr.findById(from).orElseThrow(() -> new EntryNotFoundException(from));
        Player pTo = pr.findById(to).orElseThrow(() -> new EntryNotFoundException(to));
        if (pFrom.getFriends().contains(pTo)) {
            pFrom.removeFriend(pTo);
            pr.save(pFrom);
            return true;
        }
        return false;
    }


}
