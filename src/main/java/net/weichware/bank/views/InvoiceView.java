package net.weichware.bank.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.weichware.bank.Main;
import net.weichware.bank.base.Session;
import net.weichware.bank.database.entities.Invoice;
import net.weichware.bank.database.entities.User;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Route(InvoiceView.ROUTE)
@PageTitle(Main.APPLICATION_NAME + " - " + InvoiceView.ROUTE)
public class InvoiceView extends VerticalLayout {
    public static final String ROUTE = "Abrechnungen";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getCurrencyInstance(Locale.GERMANY);
    private final User user;


    public InvoiceView() {
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

    private void userPageSetup() {
        setupInvoiceLayout(Invoice.getList(user.name()));
    }

    private void adminPageSetup() {
        setupInvoiceLayout(Invoice.getList());
    }

    private void setupInvoiceLayout(List<Invoice> invoices) {
        add(new MainMenu(user, ROUTE));
        VerticalLayout layout = new VerticalLayout();
        layout.setMaxWidth("500px");
        invoices.stream().map(this::getInvoiceCard).forEach(layout::add);
        add(layout);
    }

    private Card getInvoiceCard(Invoice invoice) {
        Card card = new Card();
        card.setWidthFull();
        card.setTitle(invoice.displayName());
        card.setSubtitle(DATE_TIME_FORMATTER.format(invoice.bookingTime()));
        card.setHeaderPrefix(new Avatar(invoice.displayName()));

        Span badge = new Span(NUMBER_FORMAT.format(invoice.bookingValue()));
        if (invoice.bookingValue() < 0) {
            badge.getElement().getThemeList().add("badge error");
        } else {
            badge.getElement().getThemeList().add("badge success");
        }
        card.setHeaderSuffix(badge);

        return card;
    }
}
