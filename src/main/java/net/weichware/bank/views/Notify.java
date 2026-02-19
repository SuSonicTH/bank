package net.weichware.bank.views;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

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
}
