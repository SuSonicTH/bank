package net.weichware.bank.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.card.CardVariant;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.weichware.bank.Main;
import net.weichware.bank.base.Session;
import net.weichware.bank.database.entities.Account;
import net.weichware.bank.database.entities.Transaction;
import net.weichware.bank.database.entities.User;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Route(AccountView.ROUTE)
@PageTitle(Main.APPLICATION_NAME + " - " + AccountView.ROUTE)
public class AccountView extends VerticalLayout {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getCurrencyInstance(Locale.GERMANY);
    public static final String ROUTE = "Konto";

    private final User user;

    public AccountView() {
        Session session = Session.get();
        user = session.user();

        if (!session.userLoggedIn()) {
            UI.getCurrent().navigate(LoginView.class);
        } else {
            if (session.user().isAdmin()) {
                adminPageSetup();
            } else {
                userPageSetup();
            }

        }
    }

    private void adminPageSetup() {
        add(new MainMenu(user, "Konten"));
        add(accountLayout(Account.getList()));
        add(new NativeLabel("Buchungen"));
        add(transactionLayout(Transaction.getOpenTransactions()));
    }

    private void userPageSetup() {
        add(new MainMenu(user, "Konto"));
        add(getBalanceCard(Account.get(user.name())));
        add(new NativeLabel("Buchungen"));
        add(transactionLayout(Transaction.getOpenTransactions(user.name())));
    }

    private VerticalLayout accountLayout(List<Account> accounts) {
        VerticalLayout layout = new VerticalLayout();
        layout.setId("accountLayout");
        layout.setMaxWidth("500px");
        accounts.stream().map(this::getAccountCard).forEach(layout::add);
        return layout;
    }

    private VerticalLayout transactionLayout(List<Transaction> transactions) {
        VerticalLayout layout = new VerticalLayout();
        layout.setId("transactionLayout");
        layout.setMaxWidth("500px");
        transactions.stream().map(this::getTransactionCard).forEach(layout::add);
        return layout;
    }

    private Card getTransactionCard(Transaction transaction) {
        Card card = new Card();
        card.setTitle(transaction.description());
        card.setSubtitle(DATE_TIME_FORMATTER.format(transaction.valueDate()));
        card.setHeaderPrefix(new Avatar(transaction.name().toUpperCase()));
        Span badge = new Span(NUMBER_FORMAT.format(transaction.bookingValue()));
        if (transaction.bookingValue() < 0) {
            badge.getElement().getThemeList().add("badge error");
        } else {
            badge.getElement().getThemeList().add("badge success");
        }
        card.setHeaderSuffix(badge);
        card.setWidthFull();
        return card;
    }

    private VerticalLayout getBalanceCard(Account account) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMaxWidth("500px");
        Card card = new Card();
        card.addThemeVariants(CardVariant.LUMO_OUTLINED);
        card.setTitle("Kontostand");
        card.setHeaderPrefix(new Avatar(account.name().toUpperCase()));
        Span badge = new Span(NUMBER_FORMAT.format(account.balance()));
        if (account.balance() < 0) {
            badge.getElement().getThemeList().add("badge error");
        } else {
            badge.getElement().getThemeList().add("badge success");
        }
        card.setHeaderSuffix(badge);
        card.setWidthFull();
        layout.add(card);
        return layout;
    }

    private Card getAccountCard(Account account) {
        Card card = new Card();
        card.setTitle(account.displayName());
        card.setHeaderPrefix(new Avatar(account.name().toUpperCase()));
        Span badge = new Span(NUMBER_FORMAT.format(account.balance()));
        if (account.balance() < 0) {
            badge.getElement().getThemeList().add("badge error");
        } else {
            badge.getElement().getThemeList().add("badge success");
        }
        card.setHeaderSuffix(badge);
        card.setWidthFull();
        return card;
    }


}
