package netcracker.study.monopoly.controller;

import lombok.extern.log4j.Log4j2;
import netcracker.study.monopoly.controller.dto.InviteMsg;
import netcracker.study.monopoly.controller.dto.RoomMsg;
import netcracker.study.monopoly.db.model.Player;
import netcracker.study.monopoly.db.repository.PlayerRepository;
import netcracker.study.monopoly.exceptions.PlayerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Controller
@Log4j2
public class RoomController {

    private final SimpMessagingTemplate template;
    private final AtomicLong lastRoomId;
    private final PlayerRepository pr;
    private final PlayerTracker playerTracker;
    private final Map<Integer, List<UUID>> usersInRooms;


    @Autowired
    public RoomController(SimpMessagingTemplate template, PlayerRepository pr, PlayerTracker playerTracker) {
        this.template = template;
        this.pr = pr;
        this.playerTracker = playerTracker;
        lastRoomId = new AtomicLong();
        usersInRooms = new HashMap<>();
    }

    @MessageMapping("/invite")
    public void getInvite(@Payload InviteMsg msg) {
        log.info(msg);
        template.convertAndSend("/topic/invite/" + msg.getTo(), msg);
    }

    @PostMapping("/rooms/create")
    public @ResponseBody
    long createRoom() {
        long roomId = lastRoomId.incrementAndGet();
        log.info("Created room " + roomId);
        return roomId;
    }

    @GetMapping("/rooms/{roomId}")
    public String loadRoom(Model model, @PathVariable String roomId, HttpSession session)
            throws PlayerNotFoundException {
        Player player = pr.findById((UUID) session.getAttribute("id"))
                .orElseThrow(PlayerNotFoundException::new);
        model.addAttribute("roomId", roomId);
        model.addAttribute("nickname", player.getNickname());
        model.addAttribute("avatar_url", player.getAvatarUrl());
        List<List<? extends Serializable>> friends = player.getFriends()
                .stream()
                .sorted((o1, o2) -> playerTracker.isOnline(o1.getNickname()) ? -1 : 1)
                .map(p -> Arrays.asList(p.getAvatarUrl(), p.getNickname(),
                        playerTracker.isOnline(p.getNickname())))
                .collect(Collectors.toList());
        model.addAttribute("friends", friends);
        return "room";
    }

    @MessageMapping("/rooms/{roomId}")
    @SendTo("/rooms/{roomId}")
    public RoomMsg sendToRoom(@Payload RoomMsg msg) {
        return msg;
    }
}
