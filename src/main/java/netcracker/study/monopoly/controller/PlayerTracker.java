package netcracker.study.monopoly.controller;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import netcracker.study.monopoly.db.model.Player;
import netcracker.study.monopoly.db.repository.PlayerRepository;
import org.apache.catalina.session.StandardSessionFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


/**
 * This filter insert into database new players and update profile if session is new
 * Also keep track of all active users (that have requests last 30 seconds)
 */
@Component
public class PlayerTracker extends GenericFilterBean {
    private final PlayerRepository playerRepository;
    private final Set<String> sessionsId = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private ExpiringMap<String, Boolean> activePlayers;

    @Autowired
    public PlayerTracker(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
        activePlayers = ExpiringMap.builder()
                .expirationPolicy(ExpirationPolicy.ACCESSED)
                .expiration(30, TimeUnit.SECONDS)
                .build();

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        StandardSessionFacade session = (StandardSessionFacade) req.getSession(false);
        // "Infinity" session
        OAuth2Authentication authentication = (OAuth2Authentication)
                SecurityContextHolder.getContext().getAuthentication();

        String name = authentication.getName();
        if (!sessionsId.contains(session.getId())) {


            Map details = (Map) authentication.getUserAuthentication().getDetails();

            Player player = playerRepository.findByNickname(name)
                    .orElseGet(() -> new Player(name));
            player.setAvatarUrl((String) details.get("avatar_url"));
            playerRepository.save(player);

            session.setMaxInactiveInterval(0);
            session.setAttribute("id", player.getId());
        }

        activePlayers.resetExpiration(name);
        activePlayers.put(name, true);
        sessionsId.add(session.getId());
        chain.doFilter(request, response);
    }

    public boolean isOnline(String name) {
        return activePlayers.containsKey(name);
    }
}
