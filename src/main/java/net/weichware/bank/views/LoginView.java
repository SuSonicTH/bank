package net.weichware.bank.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.AbstractLogin;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.weichware.bank.Main;
import net.weichware.bank.base.Authentication;
import net.weichware.bank.base.Session;
import net.weichware.bank.database.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

@Route(LoginView.ROUTE)
@PageTitle(Main.APPLICATION_NAME + " - " + LoginView.ROUTE)
public class LoginView extends VerticalLayout {
    private static final Logger log = LoggerFactory.getLogger(LoginView.class);
    public static final String ROUTE = "Login";

    private final LoginOverlay loginOverlay = new LoginOverlay();

    public LoginView() {
        loginOverlay.setTitle("Wolf Bank");
        loginOverlay.setDescription("");
        loginOverlay.addLoginListener(this::login);
        loginOverlay.setForgotPasswordButtonVisible(false);
        loginOverlay.setOpened(true);
    }

    private void login(AbstractLogin.LoginEvent loginEvent) {
        String userName = loginEvent.getUsername();
        String password = loginEvent.getPassword();

        try {
            var user = User.get(userName);
            if (user.isPresent() && Authentication.isUserAuthenticated(user.get(), password)) {
                loginOverlay.setOpened(false);
                Session.get().setUserName(userName);
                UI.getCurrent().getPage().setLocation(MainView.ROUTE);
            } else {
                loginOverlay.setError(true);
                loginOverlay.setEnabled(true);
            }
        } catch (SQLException e) {
            log.error("Error while trying to get user", e);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Error while trying authenticate user", e);
        }
    }

}
