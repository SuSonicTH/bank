package net.weichware.bank.views;

import com.vaadin.flow.component.ModalityMode;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import net.weichware.bank.base.Session;
import net.weichware.bank.database.entities.Account;
import net.weichware.bank.database.entities.Transaction;
import net.weichware.bank.database.entities.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Booking extends Dialog {
    private final Transaction transaction;
    private final User user;

    private TextField descriptionField;
    private NumberField valueField;
    private DatePicker datePicker;
    private Button saveButton;
    private Select<String> accountField;

    private boolean smallScreen;

    public Booking() {
        this(null);
    }

    public Booking(Transaction transaction) {
        this.transaction = transaction;
        user = Session.get().user();
        setupDialog();

        if (transaction != null) {
            accountField.setValue(transaction.name());
            descriptionField.setValue(transaction.description());
            valueField.setValue(transaction.bookingValue());
            datePicker.setValue(transaction.valueDate());
        }
    }

    private void setupDialog() {
        setHeaderTitle(isNew() ? "Neue Buchung" : "Buchung bearbeiten");
        getHeader().add(new Button(new Icon("lumo", "cross"), (e) -> Booking.this.close()));
        setModality(ModalityMode.STRICT);
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);

        UI.getCurrent().getPage().retrieveExtendedClientDetails(evt -> {
            if (evt.getWindowInnerWidth() < 600) {
                setTop("10px");
                smallScreen = true;
            }
        });

        VerticalLayout verticalLayout = new VerticalLayout();
        add(verticalLayout);
        verticalLayout.setMargin(false);
        verticalLayout.setSpacing(false);
        verticalLayout.setPadding(false);

        if (user.isAdmin()) {
            verticalLayout.add(createAccountField());
        }
        verticalLayout.add(createDescriptionFiled());
        verticalLayout.add(createValueField());
        verticalLayout.add(createDatePicker());

        if (!isNew()) {
            getFooter().add(createDeleteButton());
        }
        getFooter().add(createCancelButton());
        getFooter().add(createSaveButton());

        descriptionField.focus();
    }

    private Select<String> createAccountField() {
        List<String> accounts = Account.getList().stream().map(Account::name).toList();
        accountField = new Select<>();
        accountField.setLabel("Konto");
        accountField.setItems(accounts);
        return accountField;
    }

    private boolean isNew() {
        return transaction == null;
    }

    private DatePicker createDatePicker() {
        datePicker = new DatePicker("Datum", LocalDate.now());
        datePicker.setRequired(true);
        return datePicker;
    }

    private TextField createDescriptionFiled() {
        descriptionField = new TextField("Beschreibung");
        descriptionField.setRequired(true);
        return descriptionField;
    }

    private NumberField createValueField() {
        valueField = new NumberField();
        valueField.setLabel("Betrag");
        Div euroSuffix = new Div();
        euroSuffix.setText("€");
        valueField.setSuffixComponent(euroSuffix);
        valueField.setRequired(true);
        return valueField;
    }

    private Button createDeleteButton() {
        Button button = new Button("Löschen", (e) -> delete());
        button.addThemeVariants(ButtonVariant.LUMO_ERROR);
        return button;
    }

    private Button createCancelButton() {
        Button button = new Button("Abbrechen", (e) -> close());
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return button;
    }

    private Button createSaveButton() {
        saveButton = new Button("Speichern", (e) -> save());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setDisableOnClick(true);
        return saveButton;
    }

    private void delete() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);

        dialog.setHeaderTitle("Buchung löschen?");
        dialog.add(new Text("Willst du die Buchung wirklich löschen?"));

        Button cancelButton = new Button("Abbrechen", e -> dialog.close());
        cancelButton.setDisableOnClick(true);

        Button deleteButton = new Button("Löschen", e -> executeDelete());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        deleteButton.setDisableOnClick(true);

        dialog.getFooter().add(cancelButton, deleteButton);
        dialog.setWidth("400px");
        if (smallScreen) {
            dialog.setTop("100px");
        }
        dialog.open();
    }

    private void executeDelete() {
        transaction.delete();
        close();

        Notify.Success("Buchung gelöscht");
    }

    private void save() {
        String description = descriptionField.getValue().trim();
        if (description.isEmpty()) {
            validationError("Beschreibung fehlt");
            return;
        }
        Double value = valueField.getValue();
        if (value == null || value == 0) {
            validationError("Betrag fehlt");
            return;
        }
        LocalDate valueDate = datePicker.getValue();

        String account;
        if (user.isAdmin()) {
            account = accountField.getValue();
        } else {
            account = user.name();
        }

        if (isNew()) {
            new Transaction(account, LocalDateTime.now(), description, value, valueDate, "open").save();
        } else {
            transaction.update(account, description, value, valueDate);
        }
        close();

        Notify.Success(isNew() ? "Buchung gespeichert" : "Buchung aktualisiert");
    }

    @Override
    public void close() {
        UI.getCurrent().refreshCurrentRoute(true);
        super.close();
    }

    private void validationError(String errorMessage) {
        saveButton.setEnabled(true);
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setDuration(3000);
        notification.setPosition(Notification.Position.MIDDLE);

        Button closeButton = new Button(new Icon(VaadinIcon.CLOSE), (e) -> notification.close());
        var layout = new HorizontalLayout(new Text(errorMessage), closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        notification.add(layout);

        notification.open();
    }
}
