package netcracker.study.monopoly.controller;

import lombok.extern.log4j.Log4j2;
import netcracker.study.monopoly.db.DbManager;
import netcracker.study.monopoly.db.model.Player;
import netcracker.study.monopoly.db.repository.PlayerRepository;
import netcracker.study.monopoly.exceptions.EntryNotFoundException;
import netcracker.study.monopoly.exceptions.PlayerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;

@RestController
@RequestMapping("/player")
@Log4j2
public class PlayerController {

    private final DbManager dbManager;
    private final PlayerRepository pr;


    public PlayerController(DbManager dbManager, PlayerRepository pr) {
        this.dbManager = dbManager;
        this.pr = pr;
    }

    @GetMapping("/id")
    public UUID getId(HttpSession session) {
        return (UUID) session.getAttribute("id");
    }

    @GetMapping("/nickname")
    public String getNickname(Principal principal) {
        return principal.getName();
    }

    @GetMapping("/friends")
    public List<Player> getFriends(HttpSession session) throws PlayerNotFoundException {
        Player player = pr.findById((UUID) session.getAttribute("id"))
                .orElseThrow(PlayerNotFoundException::new);
        return player.getFriends();
    }

    @PostMapping("/add_friend")
    public ResponseEntity<String> addFriend(@RequestParam(name = "nickname") String nickname,
                                            Principal principal) {
        try {
            if (dbManager.addFriend(principal.getName(), nickname)) {
                return new ResponseEntity<>("Success", HttpStatus.OK);
            } else {
                return new ResponseEntity<>(
                        format("Player %s is already your friend", nickname),
                        HttpStatus.BAD_REQUEST);
            }
        } catch (PlayerNotFoundException e) {
            log.debug(e);
            return new ResponseEntity<>("Player not found", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/remove_friend")
    public ResponseEntity<String> removeFriend(@RequestParam(name = "id") UUID id,
                                               HttpSession session) {
        try {
            if (dbManager.removeFriend((UUID) session.getAttribute("id"), id)) {
                return new ResponseEntity<>("Success", HttpStatus.OK);
            } else {
                return new ResponseEntity<>(format("Friend with id %s " +
                        "not found", id), HttpStatus.BAD_REQUEST);
            }
        } catch (EntryNotFoundException e) {
            log.debug(e);
            return new ResponseEntity<>("Player not found", HttpStatus.BAD_REQUEST);
        }
    }
}
