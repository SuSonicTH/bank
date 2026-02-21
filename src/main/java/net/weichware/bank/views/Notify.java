package net.weichware.bank.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class Notify {
    public static void Success(String message) {
        Notification notification = new Notification(message);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setDuration(3000);
        notification.open();
    }

    public static void Error(String message) {
        Notification notification = new Notification(message);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setDuration(4000);
        notification.open();
    }

    public static void validationError(String errorMessage) {
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
