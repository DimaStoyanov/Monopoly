package netcracker.study.monopoly.controller;

import lombok.extern.log4j.Log4j2;
import netcracker.study.monopoly.db.DbManager;
import netcracker.study.monopoly.exceptions.EntryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.UUID;

import static java.lang.String.format;

@RestController
@RequestMapping("/player")
@Log4j2
public class PlayerController {

    private final DbManager dbManager;

    @Autowired
    public PlayerController(DbManager dbManager) {
        this.dbManager = dbManager;
    }

    @GetMapping("/id")
    public UUID getId(HttpSession session) {
        return (UUID) session.getAttribute("id");
    }

    @GetMapping("/nickname")
    public String getNickname(Principal principal) {
        return principal.getName();
    }

    @PostMapping("/add_friend")
    public ResponseEntity<String> addFriend(@RequestParam(name = "id") UUID id, HttpSession session) {
        try {
            if (dbManager.addFriend((UUID) session.getAttribute("id"), id)) {
                return new ResponseEntity<>("Success", HttpStatus.OK);
            } else {
                return new ResponseEntity<>(
                        format("Player with id %s is already your friend", id),
                        HttpStatus.BAD_REQUEST);
            }
        } catch (EntryNotFoundException e) {
            log.debug(e);
            return new ResponseEntity<>("Player not found", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
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
