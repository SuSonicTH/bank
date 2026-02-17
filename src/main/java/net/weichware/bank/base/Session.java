package net.weichware.bank.base;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.weichware.bank.database.entities.User;
import net.weichware.bank.views.LoginView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@Accessors(fluent = true)
public class Session {
    private static final Logger log = LoggerFactory.getLogger(Session.class);
    private User user;
    private boolean userLoggedIn;

    private Session(WrappedSession wrappedSession) {
        user = (User) wrappedSession.getAttribute("user");
        if (user != null) {
            userLoggedIn = true;
        }
    }

    public static Session get() {
        return new Session(VaadinSession.getCurrent().getSession());
    }

    public void setUser(User user) {
        if (user != null) {
            log.info("user {} logged in", user.name());
        }

        this.user = user;
        userLoggedIn = user != null;
        VaadinSession.getCurrent().getSession().setAttribute("user", user);
    }

    public void logout() {
        log.info("user {} logged out", this.user.name());
        setUser(null);
        UI.getCurrent().getPage().setLocation(LoginView.ROUTE);
    }

}
