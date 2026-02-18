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
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import net.weichware.bank.base.Session;
import net.weichware.bank.database.entities.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Booking extends Dialog {
    private TextField descriptionField;
    private NumberField valueField;
    private DatePicker datePicker;
    private Button saveButton;

    public Booking() {
        setHeaderTitle("Neue Buchung");
        getHeader().add(new Button(new Icon("lumo", "cross"), (e) -> Booking.this.close()));
        setModality(ModalityMode.STRICT);
        setCloseOnEsc(true);
        setDraggable(true);

        VerticalLayout verticalLayout = new VerticalLayout();
        add(verticalLayout);

        verticalLayout.add(createDescriptionFiled());
        verticalLayout.add(createValueField());
        verticalLayout.add(createDatePicker());

        getFooter().add(createCancelButton());
        getFooter().add(createSaveButton());

        descriptionField.focus();
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
        valueField.setMin(1);
        valueField.setRequired(true);
        return valueField;
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

    private void save() {
        String description = descriptionField.getValue().trim();
        if (description.isEmpty()) {
            validationError("Beschreibung fehlt");
            return;
        }
        Double value = valueField.getValue();
        if (value == null) {
            validationError("Betrag fehlt");
            return;
        }
        LocalDate valueDate = datePicker.getValue();

        new Transaction(Session.get().user().name(), LocalDateTime.now(), description, value, valueDate, "open").save();
        UI.getCurrent().refreshCurrentRoute(true);
        close();
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
