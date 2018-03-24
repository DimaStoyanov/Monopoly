package netcracker.study.monopoly.controller;


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
import java.util.stream.Collectors;

@Controller
public class ProfileController {

    private final PlayerRepository playerRepository;

    private final GameRepository gameRepository;

    @Autowired
    public ProfileController(PlayerRepository playerRepository, GameRepository gameRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
    }


    @RequestMapping("/")
    public String profile(Principal principal, Model model) {
        System.out.println("here");
        OAuth2Authentication oauth = (OAuth2Authentication) principal;
        Map details = (Map) oauth.getUserAuthentication().getDetails();
        String nickname = (String) details.get("login");
        String avatarUrl = (String) details.get("avatar_url");

        Player player = playerRepository.findByNickname(nickname).orElseGet(() ->
                new Player(nickname, avatarUrl));
        player.setOnline(true);
        playerRepository.save(player);


        List<List<? extends Serializable>> friends = player.getFriends().stream()
                .map(p -> Arrays.asList(p.getAvatarUrl(), p.getNickname(), p.isOnline()))
                .collect(Collectors.toList());
        model.addAttribute("player", nickname);
        model.addAttribute("name", details.get("name"));
        model.addAttribute("city", details.get("location"));
        model.addAttribute("avatar_url", avatarUrl);
        model.addAttribute("friends", friends);


        return "profile";

    }
}
