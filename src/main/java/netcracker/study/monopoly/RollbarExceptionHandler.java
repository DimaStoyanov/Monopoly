package netcracker.study.monopoly;

import com.rollbar.notifier.Rollbar;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.rollbar.notifier.config.ConfigBuilder.withAccessToken;


@Component
public class RollbarExceptionHandler extends AbstractHandlerExceptionResolver implements Ordered {

    private Rollbar rollbar;

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

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
        ex.printStackTrace();
        if (rollbar != null) {
            rollbar.debug(ex);
        }
        return null;
    }
}
