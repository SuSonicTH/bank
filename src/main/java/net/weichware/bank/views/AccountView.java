package net.weichware.bank.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.weichware.bank.Main;
import net.weichware.bank.base.Session;
import net.weichware.bank.database.entities.Account;
import net.weichware.bank.database.entities.Transaction;
import net.weichware.bank.database.entities.User;


@Route(AccountView.ROUTE)
@PageTitle(Main.APPLICATION_NAME + " - " + AccountView.ROUTE)
public class AccountView extends VerticalLayout {
    public static final String ROUTE = "Konto";
    private final User user;

    public AccountView() {
        Session session = Session.get();
        user = session.user();

        if (!session.userLoggedIn()) {
            UI.getCurrent().getPage().setLocation(LoginView.ROUTE);
        } else {
            if (session.user().isAdmin()) {
                adminPageSetup();
            } else {
                userPageSetup();
            }

        }

    }

    private void adminPageSetup() {
        add(new NativeLabel("Konten"));
        for (Account account : Account.getList()) {
            add(new NativeLabel(account.name() + ": " + account.balance()));
        }

        Grid<Transaction> grid = new Grid<>(Transaction.class, false);
        grid.addColumn(Transaction::name).setHeader("Konto");
        grid.addColumn(Transaction::bookingTime).setHeader("Buchungs Zeit");
        grid.addColumn(Transaction::description).setHeader("Buchungs Text");
        grid.addColumn(Transaction::valueDate).setHeader("Valuten Datum");
        grid.addColumn(Transaction::bookingValue).setHeader("Betrag");
        grid.setItems(Transaction.getOpenTransactions());

        add(grid);
    }

    private void userPageSetup() {
        add(new NativeLabel("Guthaben : " + Account.get(user.name()).balance()));

        Grid<Transaction> grid = new Grid<>(Transaction.class, false);
        grid.addColumn(Transaction::bookingTime).setHeader("Buchungs Zeit");
        grid.addColumn(Transaction::description).setHeader("Buchungs Text");
        grid.addColumn(Transaction::valueDate).setHeader("Valuten Datum");
        grid.addColumn(Transaction::bookingValue).setHeader("Betrag");
        grid.setItems(Transaction.getOpenTransactions(user.name()));

        add(grid);
    }
}
