package SimpleYesChat.YesChat.Messages.notifications;


import SimpleYesChat.YesChat.Messages.YesChatMessages;
import SimpleYesChat.YesChat.Messages.answers.StatusContacter;
import org.springframework.web.socket.WebSocketSession;

public class UsersChangeStatusNotification extends Notification  {
    @Override
    public void init(YesChatMessages messages) {

    }

    StatusContacter statusContacter;
    String idContacter;

    @Override
    public void execute(WebSocketSession session) {

    }

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
