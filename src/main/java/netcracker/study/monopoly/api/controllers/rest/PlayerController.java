package netcracker.study.monopoly.api.controllers.rest;

import lombok.extern.log4j.Log4j2;
import netcracker.study.monopoly.api.controllers.filters.PlayerTracker;
import netcracker.study.monopoly.api.dto.PlayerInfo;
import netcracker.study.monopoly.converters.PlayerInfoConverter;
import netcracker.study.monopoly.exceptions.PlayerNotFoundException;
import netcracker.study.monopoly.models.entities.Player;
import netcracker.study.monopoly.models.repositories.PlayerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/player")
@Log4j2
public class PlayerController {

    private final PlayerRepository playerRepository;
    private final PlayerTracker playerTracker;
    private final PlayerInfoConverter playerInfoConverter;

    public PlayerController(PlayerRepository playerRepository, PlayerTracker playerTracker,
                            PlayerInfoConverter playerInfoConverter) {
        this.playerRepository = playerRepository;
        this.playerTracker = playerTracker;
        this.playerInfoConverter = playerInfoConverter;
    }

    @GetMapping("/id")
    public UUID getId(HttpSession session) {
        return (UUID) session.getAttribute("id");
    }

    @GetMapping("/nickname")
    public String getNickname(Principal principal) {
        return principal.getName();
    }

    @GetMapping("/avatar_url")
    public String getAvatarUrl(Principal principal) {
        Player player = playerRepository.findByNickname(principal.getName())
                .orElseThrow(PlayerNotFoundException::new);
        return player.getAvatarUrl();
    }

    @GetMapping("/friends")
    public List<PlayerInfo> getFriends(HttpSession session) {
        Player player = getPlayer(session);
        return playerInfoConverter.toDtoAll(player.getFriends());
    }

    private Player getPlayer(HttpSession session) {
        return playerRepository.findById((UUID) session.getAttribute("id"))
                .orElseThrow(PlayerNotFoundException::new);
    }

    @GetMapping("/info")
    public PlayerInfo getInfo(HttpSession session) {
        Player player = getPlayer(session);
        return playerInfoConverter.toDto(player);
    }

    @PutMapping("/add_friend")
    public ResponseEntity<String> addFriend(@RequestParam(name = "nickname") String nickname,
                                            Principal principal) {
        Player playerFrom = playerRepository.findByNickname(principal.getName())
                .orElseThrow(PlayerNotFoundException::new);
        Player playerTo = playerRepository.findByNickname(nickname)
                .orElseThrow(PlayerNotFoundException::new);
        if (playerFrom.getFriends().contains(playerTo)) {
            return new ResponseEntity<>("Already friends", HttpStatus.BAD_REQUEST);
        }
        playerFrom.addFriend(playerTo);
        playerRepository.save(playerFrom);
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    @PutMapping("/remove_friend")
    public ResponseEntity<String> removeFriend(@RequestParam(name = "nickname") String nickname,
                                               Principal principal) {
        String name = principal.getName();
        Player playerFrom = playerRepository.findByNickname(name)
                .orElseThrow(PlayerNotFoundException::new);
        Player playerTo = playerRepository.findByNickname(nickname)
                .orElseThrow(PlayerNotFoundException::new);
        if (playerFrom.getFriends().contains(playerTo)) {
            playerFrom.removeFriend(playerTo);
            playerRepository.save(playerFrom);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Can't find friend " + nickname, HttpStatus.BAD_REQUEST);
        }
    }
}
