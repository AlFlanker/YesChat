package SimpleYesChat.YesChat.Messages.notifications;


import java.util.List;

public class UsersChangeStatusNotification extends Notification{
    private List<String> listStatus;

    public List<String> getListStatus() {
        return listStatus;
    }

    public void setListStatus(List<String> listStatus) {
        this.listStatus = listStatus;
    }
}

