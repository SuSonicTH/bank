package net.weichware.bank.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.weichware.bank.Main;
import net.weichware.bank.base.Session;


@Route(MainView.ROUTE)
@PageTitle(Main.APPLICATION_NAME + " - " + MainView.ROUTE)
public class MainView extends VerticalLayout {
    public static final String ROUTE = "Konto";

    public MainView() {
        if (!Session.get().userLoggedIn()) {
            UI.getCurrent().getPage().setLocation(LoginView.ROUTE);
        } else {
            pageSetup();
        }

    }

    private void pageSetup() {
        add(new NativeLabel("Kontoübersicht"));
    }
}
