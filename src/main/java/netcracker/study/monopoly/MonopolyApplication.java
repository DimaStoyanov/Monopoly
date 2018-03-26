package netcracker.study.monopoly;

import netcracker.study.monopoly.controller.PlayerTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

@SpringBootApplication
@ComponentScan
@EnableAutoConfiguration
@EnableOAuth2Sso
public class MonopolyApplication extends WebSecurityConfigurerAdapter {

    private final PlayerTracker playerTracker;

    @Autowired
    public MonopolyApplication(PlayerTracker playerTracker) {
        this.playerTracker = playerTracker;
    }

    public static void main(String[] args) {
        SpringApplication.run(MonopolyApplication.class, args);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http
                .addFilterAfter(playerTracker, FilterSecurityInterceptor.class)
                .sessionManagement().maximumSessions(1);
    }


}




