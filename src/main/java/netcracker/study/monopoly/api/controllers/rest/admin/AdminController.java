package netcracker.study.monopoly.api.controllers.rest.admin;

import netcracker.study.monopoly.models.entities.Player;
import netcracker.study.monopoly.models.repositories.PlayerRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final PlayerRepository playerRepository;

    private static final List<String> ADMINS = Collections.unmodifiableList(Arrays.asList(
            "DimaStoyanov", "Kest1996", "bonart6"
    ));

    public AdminController(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }


    @GetMapping("/authorities")
    public Collection<GrantedAuthority> getAuthorities(OAuth2Authentication authentication) {
        return authentication.getAuthorities();
    }

    @GetMapping("/insert")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String insertPlayer(@RequestParam(name = "nickname") String nickname) {
        playerRepository.save(new Player(nickname));
        return "Success";
    }

    @GetMapping("/upgrade")
    public String upgradeAuthorities() {
        OAuth2Authentication authentication =
                (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        if (!ADMINS.contains(authentication.getName())) {
            return "Not allowed";
        }
        Collection<GrantedAuthority> authorities =
                authentication.getAuthorities();
        SimpleGrantedAuthority admin = new SimpleGrantedAuthority("ROLE_ADMIN");
        List<GrantedAuthority> updatedAuthorities = new ArrayList<>();
        updatedAuthorities.add(admin);
        updatedAuthorities.addAll(authorities);
        SecurityContextHolder.getContext()
                .setAuthentication(
                        new OAuth2Authentication(
                                authentication.getOAuth2Request(), new Authentication() {
                            @Override
                            public Collection<? extends GrantedAuthority> getAuthorities() {
                                return updatedAuthorities;
                            }

                            @Override
                            public Object getCredentials() {
                                return authentication.getCredentials();
                            }

                            @Override
                            public Object getDetails() {
                                return authentication.getDetails();
                            }

                            @Override
                            public Object getPrincipal() {
                                return authentication.getPrincipal();
                            }

                            @Override
                            public boolean isAuthenticated() {
                                return authentication.isAuthenticated();
                            }

                            @Override
                            public void setAuthenticated(boolean isAuthenticated) {
                                authentication.setAuthenticated(isAuthenticated);
                            }

                            @Override
                            public String getName() {
                                return authentication.getName();
                            }
                        }
                        )
                );
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
    }

}

