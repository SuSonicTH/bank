package net.weichware.bank.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import net.weichware.bank.base.Authentication;
import net.weichware.bank.base.Session;
import net.weichware.bank.database.entities.Account;
import net.weichware.bank.database.entities.User;
import net.weichware.bank.views.Notify;

import java.util.List;
import java.util.Optional;

public class NewPassword extends BaseDialog {
    private final User user;
    private final boolean reset;
    private Select<String> accountField;
    private PasswordField oldPasswordField;
    private PasswordField newPasswordField;
    private PasswordField repeatPasswordField;
    private Button saveButton;

    public NewPassword(boolean reset) {
        super("Passwort ändern");
        this.reset = reset;
        user = Session.get().user();
        setupDialog();
    }

    private void setupDialog() {
        if (reset) {
            List<String> accounts = Account.getList().stream().map(Account::name).filter(name -> !name.equals(user.name())).toList();
            accountField = new Select<>();
            accountField.setLabel("Benutzer");
            accountField.setItems(accounts);
            layout.add(accountField);
        } else {
            oldPasswordField = new PasswordField();
            oldPasswordField.setLabel("Altes Passwort");
            layout.add(oldPasswordField);
        }
        newPasswordField = new PasswordField();
        newPasswordField.setLabel("Neues Passwort");
        layout.add(newPasswordField);

        repeatPasswordField = new PasswordField();
        repeatPasswordField.setLabel("Password wiederholen");
        layout.add(repeatPasswordField);

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
