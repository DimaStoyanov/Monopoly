package netcracker.study.monopoly.controller;


import lombok.SneakyThrows;
import netcracker.study.monopoly.db.model.Player;
import netcracker.study.monopoly.db.repository.GameRepository;
import netcracker.study.monopoly.db.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.Serializable;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Controller
public class ProfileController {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final PlayerTracker playerTracker;

    @Autowired
    public ProfileController(PlayerRepository playerRepository, GameRepository gameRepository, PlayerTracker playerTracker) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.playerTracker = playerTracker;
    }


    @RequestMapping("/")
    @SneakyThrows
    public String profile(Principal principal, Model model) {
        OAuth2Authentication oauth = (OAuth2Authentication) principal;
        Map details = (Map) oauth.getUserAuthentication().getDetails();
        String nickname = principal.getName();

        Player player = playerRepository.findByNickname(nickname).orElseThrow(() ->
                new NoSuchElementException(String.format("Player %s not found in db", nickname)));


        List<List<? extends Serializable>> friends = player.getFriends().stream()
                // Online players should be first
                .sorted((o1, o2) -> playerTracker.isOnline(o1.getNickname()) ? -1 : 1)
                .map(p -> Arrays.asList(p.getAvatarUrl(), p.getNickname(),
                        playerTracker.isOnline(p.getNickname())))
                .collect(Collectors.toList());


        model.addAttribute("player", nickname);
        model.addAttribute("name", details.get("name"));
        model.addAttribute("city", details.get("location"));
        model.addAttribute("avatar_url", player.getAvatarUrl());
        model.addAttribute("friends", friends);


        return "profile";

    }
}
