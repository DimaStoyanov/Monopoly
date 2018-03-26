package netcracker.study.monopoly.listener;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OnlinePlayerHolder implements HttpSessionListener {


    private static Set<String> activeSessionIds = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        System.out.println("Session created");
        se.getSession().setMaxInactiveInterval(60);
        activeSessionIds.add(se.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        System.out.println("Session destroyed");
        activeSessionIds.remove(se.getSession().getId());
    }

    public boolean isSessionActive(String sessionId) {
        return activeSessionIds.contains(sessionId);
    }

}
