package net.weichware.bank.views;

import com.vaadin.flow.component.ModalityMode;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import net.weichware.bank.base.Authentication;
import net.weichware.bank.base.Session;
import net.weichware.bank.database.entities.Account;
import net.weichware.bank.database.entities.User;

import java.util.List;
import java.util.Optional;

public class NewPassword extends Dialog {
    private final User user;
    private final boolean reset;
    private Select<String> accountField;
    private PasswordField oldPasswordField;
    private PasswordField newPasswordField;
    private PasswordField repeatPasswordField;
    private Button saveButton;

    public NewPassword(boolean reset) {
        this.reset = reset;
        user = Session.get().user();
        setupDialog();
    }

    private void setupDialog() {
        setHeaderTitle("Passwort ändern");
        getHeader().add(new Button(new Icon("lumo", "cross"), (e) -> close()));
        setModality(ModalityMode.STRICT);
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);

        UI.getCurrent().getPage().retrieveExtendedClientDetails(evt -> {
            if (evt.getWindowInnerWidth() < 600) {
                setTop("10px");
            }
        });

        VerticalLayout verticalLayout = new VerticalLayout();
        add(verticalLayout);
        verticalLayout.setMargin(false);
        verticalLayout.setSpacing(false);
        verticalLayout.setPadding(false);

        if (reset) {
            List<String> accounts = Account.getList().stream().map(Account::name).filter(name -> !name.equals(user.name())).toList();
            accountField = new Select<>();
            accountField.setLabel("Benutzer");
            accountField.setItems(accounts);
            verticalLayout.add(accountField);
        } else {
            oldPasswordField = new PasswordField();
            oldPasswordField.setLabel("Altes Passwort");
            verticalLayout.add(oldPasswordField);
        }
        newPasswordField = new PasswordField();
        newPasswordField.setLabel("Neues Passwort");
        verticalLayout.add(newPasswordField);

        repeatPasswordField = new PasswordField();
        repeatPasswordField.setLabel("Password wiederholen");
        verticalLayout.add(repeatPasswordField);

        saveButton = new Button("Speichern", (e) -> save());
        saveButton.setDisableOnClick(true);
        getFooter().add(saveButton);
    }

    private void save() {
        if (!reset && !Authentication.isUserAuthenticated(user, oldPasswordField.getValue())) {
            saveButton.setEnabled(true);
            Notify.validationError("Das alte Password stimmt nicht");
            return;
        }
        if (!newPasswordField.getValue().equals(repeatPasswordField.getValue())) {
            saveButton.setEnabled(true);
            Notify.validationError("Das neue Password stimmt nitcht mit der Wiederholung überein");
            return;
        }

        User updateUser = user;
        if (reset) {
            Optional<User> foundUser = User.get(accountField.getValue());
            if (foundUser.isEmpty()) {
                saveButton.setEnabled(true);
                Notify.validationError("Benutzer " + accountField.getValue() + " nicht gefunden");
                return;
            }
            updateUser = foundUser.get();
        }
        if (user.setPassword(updateUser.name(), newPasswordField.getValue())) {
            Notify.Success("Passwort geändert");
        } else {
            Notify.Error("Interner Fehler, Passwort NICHT geändert");
        }
        close();
    }
}
