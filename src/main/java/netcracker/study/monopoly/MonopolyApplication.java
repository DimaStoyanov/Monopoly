package netcracker.study.monopoly;

import com.rollbar.notifier.Rollbar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.rollbar.notifier.config.ConfigBuilder.withAccessToken;

@SpringBootApplication
@ComponentScan
@EnableAutoConfiguration
@EnableOAuth2Sso
public class MonopolyApplication extends AbstractHandlerExceptionResolver {

    private static Rollbar rollbar;


    public static void main(String[] args) {
        SpringApplication.run(MonopolyApplication.class, args);

        String token = System.getenv("ROLLBAR_ACCESS_TOKEN");
        if (token != null) {
            rollbar = Rollbar.init(withAccessToken(token)
                    .handleUncaughtErrors(true)
                    .build());
        }
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response,
                                              Object handler, Exception ex) {
        if (rollbar != null) {
            rollbar.debug(ex);
        }
        return null;
    }


    @Configuration
    @Order(101)
    class WebSecConfig extends WebSecurityConfigurerAdapter {

        @Bean
        public SessionRegistry sessionRegistry() {
            return new SessionRegistryImpl();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.sessionManagement().maximumSessions(1).sessionRegistry(sessionRegistry());
        }
    }

}
