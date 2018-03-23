package netcracker.study.monopoly;

import com.rollbar.notifier.Rollbar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

import static com.rollbar.notifier.config.ConfigBuilder.withAccessToken;

@SpringBootApplication
@ComponentScan
@EnableAutoConfiguration
//@EnableOAuth2Sso
@RestController
public class MonopolyApplication extends AbstractHandlerExceptionResolver {

    private static Rollbar rollbar;


    @RequestMapping("/get_user")
    public Principal user(Principal principal) {

        System.out.println(principal);
        return principal;
    }


    @RequestMapping("/ok")
    public String ok() {
        return "OK";
    }

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

}
