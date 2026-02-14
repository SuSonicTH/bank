package net.weichware.bank.base;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.WrappedSession;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.weichware.bank.views.LoginView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@Accessors(fluent = true)
public class Session {
    private static final Logger log = LoggerFactory.getLogger(Session.class);
    private String userName;
    private boolean userLoggedIn;

    private Session(WrappedSession wrappedSession) {
        userName = (String) wrappedSession.getAttribute("userName");
        if (userName != null) {
            userLoggedIn = true;
        }
    }

    public static Session get() {
        return new Session(VaadinService.getCurrentRequest().getWrappedSession());
    }

    public void setUserName(String userName) {
        if (userName != null) {
            log.info("user {} logged in", userName);
        }

        this.userName = userName;
        userLoggedIn = userName != null;
        VaadinService.getCurrentRequest().getWrappedSession().setAttribute("userName", userName);
    }

    public void logout() {
        log.info("user {} logged out", this.userName);
        setUserName(null);
        UI.getCurrent().getPage().setLocation(LoginView.ROUTE);
    }
}
