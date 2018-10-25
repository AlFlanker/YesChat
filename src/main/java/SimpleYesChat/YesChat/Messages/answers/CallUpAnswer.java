package SimpleYesChat.YesChat.Messages.answers;


import SimpleYesChat.YesChat.Messages.YesChatMessages;
import SimpleYesChat.YesChat.UserData.GlobalData;
import SimpleYesChat.YesChat.UserData.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
@Component
public class CallUpAnswer extends Message  {

    @Autowired
    private GlobalData globalData;

    @Override
    public void init(YesChatMessages messages) {
        this.fromID = ((CallUpAnswer)messages).fromID;
        this.statusContacter = ((CallUpAnswer)messages).statusContacter;
        this.toID =  ((CallUpAnswer)messages).toID;
    }

    private String fromID;
    private StatusContacter statusContacter;

    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    public StatusContacter getStatusContacter() {
        return statusContacter;
    }

    public void setStatusContacter(StatusContacter statusContacter) {
        this.statusContacter = statusContacter;
    }


    @Override
    public void execute(WebSocketSession session) {
        callUp(this,session);

    }
    protected void callUp(CallUpAnswer request, WebSocketSession session) {
        Map.Entry<WebSocketSession, UserData> entry = globalData.getSessions().entrySet().stream()
                .filter(e->e.getValue().getId().equals(request.getToID()))
                .findFirst().get();
        if(entry.getValue().isAuth()){
            request.setFromID(globalData.getSessions().get(session).getId());
            sendResponse(request,entry.getKey());
        }
        else{
            CallUpAnswer call = new CallUpAnswer();
            call.setStatusContacter(StatusContacter.OFFLINE);
            call.setFromID(request.getFromID());
            call.setToID(request.getToID());
            sendResponse(call,session);
        }
    }

}
