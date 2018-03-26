package netcracker.study.monopoly;

import com.rollbar.notifier.Rollbar;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.rollbar.notifier.config.ConfigBuilder.withAccessToken;


// TODO - not work
@Component
public class RollbarExceptionHandler extends AbstractHandlerExceptionResolver {

    private Rollbar rollbar;

    RollbarExceptionHandler() {
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
        System.out.println(ex.getMessage());
        if (rollbar != null) {
            rollbar.debug(ex);
        }
        return null;
    }
}
