package net.weichware.bank.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class NoRoute extends VerticalLayout {
    public NoRoute() {
        UI.getCurrent().getPage().setLocation(LoginView.ROUTE);
    }
}
