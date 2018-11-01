package SimpleYesChat.YesChat.Messages.answers;


import SimpleYesChat.YesChat.Messages.YesChatMessages;
import SimpleYesChat.YesChat.UserData.GlobalData;
import SimpleYesChat.YesChat.UserData.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
@Scope("prototype")
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
        if(request.getStatusContacter()!=StatusContacter.BUSY || request.getStatusContacter()!=StatusContacter.OFFLINE) {
                request.setFromID(globalData.getSessions().get(session).getId());
                sendResponse(request, entry.getKey());
                globalData.getSessions().get(session).setBusy(true);
        }
        else {
            CallUpAnswer call = new CallUpAnswer();
            call.setStatusContacter(request.getStatusContacter());
            call.setFromID(request.getFromID());
            call.setToID(request.getToID());
            sendResponse(call, session);
        }
    }

}
