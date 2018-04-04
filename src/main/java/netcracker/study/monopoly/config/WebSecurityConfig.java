package netcracker.study.monopoly.config;

import netcracker.study.monopoly.api.controllers.filters.PlayerTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

@Configuration
@EnableOAuth2Sso
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    private final PlayerTracker playerTracker;


    @Autowired
    public WebSecurityConfig(PlayerTracker playerTracker) {
        this.playerTracker = playerTracker;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http
//                .httpBasic().disable()
                .addFilterAfter(playerTracker, FilterSecurityInterceptor.class)
                .csrf().disable()
                .sessionManagement().maximumSessions(1);
    }
}
