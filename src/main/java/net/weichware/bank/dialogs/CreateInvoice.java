package net.weichware.bank.dialogs;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.select.Select;
import net.weichware.bank.database.entities.Account;
import net.weichware.bank.database.entities.Invoice;
import net.weichware.bank.views.InvoiceView;

import java.util.ArrayList;
import java.util.List;

public class CreateInvoice extends BaseDialog {
    public static final String ALLE_KONTEN = "Alle Konten";
    private final Select<String> accountField = new Select<>();
    private final List<String> accounts;

    public CreateInvoice() {
        super("Abrechnung erstellen");
        accounts = Account.getList().stream().map(Account::name).toList();
        layout.add(createAccountsField());
        getFooter().add(createCancelButton());
        getFooter().add(createStartInvoice());
    }

    private Select<String> createAccountsField() {
        accountField.setLabel("Konto");
        List<String> selectionList = new ArrayList<>();
        selectionList.add(ALLE_KONTEN);
        selectionList.addAll(accounts);
        accountField.setItems(selectionList);
        accountField.setValue(ALLE_KONTEN);
        return accountField;
    }

    private Button createCancelButton() {
        Button button = new Button("Abbrechen", (e) -> close());
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return button;
    }

    private Button createStartInvoice() {
        Button startInvoice = new Button("Abrechnen", (e) -> invoice());
        startInvoice.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        startInvoice.setDisableOnClick(true);
        return startInvoice;
    }

    private void invoice() {
        if (ALLE_KONTEN.equals(accountField.getValue())) {
            accounts.forEach(Invoice::createInvoice);
        } else {
            Invoice.createInvoice(accountField.getValue());
        }
        close();
        UI.getCurrent().navigate(InvoiceView.ROUTE);
    }
}
