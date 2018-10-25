package SimpleYesChat.YesChat.Messages.notifications;


import SimpleYesChat.YesChat.Messages.answers.StatusContacter;

public class UsersChangeStatusNotification extends Notification  {
    StatusContacter statusContacter;
    String idContacter;

    public StatusContacter getStatusContacter() {
        return statusContacter;
    }

    public void setStatusContacter(StatusContacter statusContacter) {
        this.statusContacter = statusContacter;
    }

    public String getIdContacter() {
        return idContacter;
    }

    public void setIdContacter(String idContacter) {
        this.idContacter = idContacter;
    }


}
