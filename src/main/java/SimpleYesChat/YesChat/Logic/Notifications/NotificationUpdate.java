package SimpleYesChat.YesChat.Logic.Notifications;

import java.util.List;

public class NotificationUpdate extends Notification{
    private List<String> contacters;

    public List<String> getContacters() {
        return contacters;
    }

    public void setContacters(List<String> contacters) {
        this.contacters = contacters;
    }

    public NotificationUpdate() {
    }
}
