package net.weichware.bank.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import net.weichware.bank.base.Session;
import net.weichware.bank.database.entities.User;
import net.weichware.bank.dialogs.CreateInvoice;
import net.weichware.bank.dialogs.NewPassword;


public class MainMenu extends HorizontalLayout {

    public MainMenu(User user, String title) {
        MenuBar menuBar = new MenuBar();
        add(menuBar);
        Span label = new Span(title + " - " + user.name());
        label.addClassName("titleLabel");
        add(label);
        createMenu(user, menuBar);

    }

    private static void createMenu(User user, MenuBar menuBar) {
        SubMenu menu = menuBar.addItem(new Icon(VaadinIcon.MENU)).getSubMenu();
        if (user.isAdmin()) {
            menu.addItem("Benutzer", (event) -> Notification.show("Benutzer", 5000, Notification.Position.TOP_START));
            menu.addItem("Benutzer Passwort zurücksetzen", (event) -> new NewPassword(true).open());
            menu.addItem("Konten", (event) -> UI.getCurrent().navigate(AccountView.ROUTE));
            menu.addItem("Abrechnung erstellen", (event) -> new CreateInvoice().open());
        } else {
            menu.addItem("Konto", (event) -> UI.getCurrent().navigate(AccountView.ROUTE));
        }
        menu.addItem("Abrechnungen", (event) -> UI.getCurrent().navigate(InvoiceView.ROUTE));
        menu.addItem("Passwort ändern", (event) -> new NewPassword(false).open());
        menu.addItem("Abmelden", (event) -> Session.get().logout());
    }
}
