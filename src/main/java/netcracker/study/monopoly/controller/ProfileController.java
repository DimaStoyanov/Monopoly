package netcracker.study.monopoly.controller;


import netcracker.study.monopoly.db.model.Player;
import netcracker.study.monopoly.db.repository.GameRepository;
import netcracker.study.monopoly.db.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class ProfileController {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final SessionRegistry sessionRegistry;


    @Autowired
    public ProfileController(PlayerRepository playerRepository, GameRepository gameRepository, SessionRegistry sessionRegistry) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.sessionRegistry = sessionRegistry;
    }


    @RequestMapping("/")
    public String profile(Principal principal, HttpSession session, Model model) {

        OAuth2Authentication oauth = (OAuth2Authentication) principal;
        Map details = (Map) oauth.getUserAuthentication().getDetails();
        String nickname = (String) details.get("login");
        String avatarUrl = (String) details.get("avatar_url");

        Player player = playerRepository.findByNickname(nickname).orElseGet(() ->
                new Player(nickname));
        player.setAvatarUrl(avatarUrl);
        playerRepository.save(player);

        Set<String> active = sessionRegistry.getAllPrincipals().stream()
                .filter(u -> !sessionRegistry.getAllSessions(u, false).isEmpty())
                .map(Object::toString)
                .collect(Collectors.toSet());

        sessionRegistry.getAllPrincipals().stream()
                .map(p -> (Principal) p)
                .forEach(p -> System.out.println(p.getName()));

        List<List<? extends Serializable>> friends = player.getFriends().stream()
                .sorted((o1, o2) -> active.contains(o1.getNickname()) ? -1 : 1)
                .map(p -> Arrays.asList(p.getAvatarUrl(), p.getNickname(), active.contains(p.getNickname())))
                .collect(Collectors.toList());


        model.addAttribute("player", nickname);
        model.addAttribute("name", details.get("name"));
        model.addAttribute("city", details.get("location"));
        model.addAttribute("avatar_url", avatarUrl);
        model.addAttribute("friends", friends);


        return "profile";

    }
}
