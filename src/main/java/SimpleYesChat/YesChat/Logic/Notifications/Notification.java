package SimpleYesChat.YesChat.Logic.Notifications;

import SimpleYesChat.YesChat.Logic.Notifications.Enums.NotificationType;

public class Notification {
    private NotificationType type;

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public Notification() {
    }
}
