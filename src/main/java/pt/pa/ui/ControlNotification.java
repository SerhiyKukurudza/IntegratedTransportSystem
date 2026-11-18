/******************************************************************************************************************************
 * File Name: ControlNotification.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.ui;

import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Represents a UI class with static methods to show notifications from the ControlFX library.
 */
public class ControlNotification {
    /**
     * Represents the size of the icon of the notification types that possess an icon.
     */
    private static final int ICON_SIZE = 40;

    /**
     * Represents the type of notification that can be shown in the ControlFX Notification.
     */
    public enum NotificationType {
        /**
         * Represents type of the notification that has no icon.
         */
        DEFAULT(null, null),
        /**
         * Represents the information type notification icon and color.
         */
        INFORMATION("mdi-information", "blue"),
        /**
         * Represents the success type notification icon and color.
         */
        SUCCESS("mdi-check-circle", "green"),
        /**
         * Represents the error type notification icon and color.
         */
        ERROR("mdi-close-circle", "red"),
        /**
         * Represents the warning type notification icon and color.
         */
        WARNING("mdi-alert-circle", "orange"),
        /**
         * Represents the welcome type notification icon and color.
         */
        GREETING("mdi-human-greeting", "black");

        /**
         * The icon code associated with the notification type.
         */
        private final String iconCode;

        /**
         * The color associated with the notification type.
         */
        private final String color;

        /**
         * Constructs a NotificationType with the specified icon code and color.
         *
         * @param iconCode The icon code representing the notification type.
         * @param color The color of the notification icon.
         */
        NotificationType(String iconCode, String color) {
            this.iconCode = iconCode;
            this.color = color;
        }

        /**
         * Gets the icon code associated with the notification type.
         *
         * @return The icon code.
         */
        public String getIconCode() {
            return iconCode;
        }

        /**
         * Gets the color associated with the notification type.
         *
         * @return The color of the notification icon.
         */
        public String getColor() {
            return color;
        }
    }

    /**
     * Represents the window where the notification will be shown.
     */
    private static Window window;

    /**
     * Sets the window where the notification will be shown.
     *
     * @param window The window where the notification will be displayed.
     */
    public static void setWindow(Window window){
        ControlNotification.window = window;
    }

    /**
     * Shows a notification with only a title.
     *
     * @param title The title of the notification.
     */
    public static void show(String title) {
        show(title, null, NotificationType.DEFAULT, Pos.TOP_RIGHT);
    }

    /**
     * Shows a notification with a title and content.
     *
     * @param title The title of the notification.
     * @param content The content of the notification.
     */
    public static void show(String title, String content) {
        show(title, content, NotificationType.DEFAULT, Pos.TOP_RIGHT);
    }

    /**
     * Shows a notification with a title, content, and a specific notification type.
     *
     * @param title The title of the notification.
     * @param content The content of the notification.
     * @param type The type of the notification (e.g., INFORMATION, WARNING, etc.).
     */
    public static void show(String title, String content, NotificationType type) {
        show(title, content, type, Pos.TOP_RIGHT);
    }

    /**
     * Shows a notification with a title, content, notification type, and a specific position.
     *
     * @param title The title of the notification.
     * @param content The content of the notification.
     * @param type The type of the notification.
     * @param position The position of the notification on the screen.
     */
    public static void show(String title, String content, NotificationType type, Pos position) {
        Notifications notification = Notifications.create()
                .position(position)
                .owner(window)
                .hideAfter(Duration.seconds(5.0));

        if (title != null && !title.isBlank()) {
            notification.title(title);
        }

        if (content != null && !content.isBlank()) {
            notification.text(content);
        }

        notification.styleClass("control-notification");

        showNotificationByType(notification, type);
    }

    /**
     * Returns a formatted icon from the notification type.
     *
     * @param type The type of the notification.
     * @return The icon with correct code and color.
     */
    private static FontIcon getFormattedIconByType(NotificationType type) {
        FontIcon fontIcon = new FontIcon(type.getIconCode());
        fontIcon.setIconSize(ICON_SIZE);
        fontIcon.setIconColor(Color.web(type.getColor()));

        return fontIcon;
    }

    /**
     * Shows a control notification according to the provided type.
     *
     * @param notification The notification to show.
     * @param type The type of notification that should be shown.
     */
    private static void showNotificationByType(Notifications notification, NotificationType type) {
        if(type != NotificationType.DEFAULT) {
            notification.graphic(getFormattedIconByType(type));
            notification.styleClass("icon-notification");
        }

        notification.show();
    }
}
