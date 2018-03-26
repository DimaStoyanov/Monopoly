package netcracker.study.monopoly;

import com.rollbar.notifier.Rollbar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.rollbar.notifier.config.ConfigBuilder.withAccessToken;

@SpringBootApplication
@ComponentScan
@EnableAutoConfiguration
@EnableOAuth2Sso
@Configuration
public class MonopolyApplication /*extends AbstractHandlerExceptionResolver*/
        extends WebSecurityConfigurerAdapter {

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

    //    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response,
                                              Object handler, Exception ex) {
        if (rollbar != null) {
            rollbar.debug(ex);
        }
        return null;
    }


    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.sessionManagement().maximumSessions(1).sessionRegistry(sessionRegistry());
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> publisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }


}
