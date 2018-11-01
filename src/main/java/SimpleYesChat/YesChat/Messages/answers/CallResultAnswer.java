package SimpleYesChat.YesChat.Messages.answers;


import SimpleYesChat.YesChat.Messages.YesChatMessages;
import SimpleYesChat.YesChat.UserData.GlobalData;
import SimpleYesChat.YesChat.UserData.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Optional;

@Scope("prototype")
@Component
public class CallResultAnswer extends Message  {
    @Autowired
    private GlobalData globalData;

    @Override
    public void execute(WebSocketSession session) {
        Map.Entry<WebSocketSession, UserData> entry = globalData.getSessions().entrySet().stream()
                .filter(e->e.getValue().getId().equals(this.getToID()))
                .findFirst().get();
        Optional<WebSocketSession> socketSession = globalData.getSessions().entrySet()
                .stream().filter(e -> e.getValue().equals(this.toID))
                .map(e -> {
                    return e.getKey();
                })
                .findFirst();
        if(socketSession.isPresent()){
            globalData.getSessions().get(socketSession.get()).setBusy(false);
        }
        globalData.getSessions().get(session).setBusy(false);
        sendResponse(this,entry.getKey());
    }

    @Override
    public void init(YesChatMessages messages) {
        this.callResult = ((CallResultAnswer)messages).callResult;
        this.toID = ((CallResultAnswer)messages).toID;
        this.fromID = ((CallResultAnswer)messages).fromID;

    }



    private CallResult callResult;
    private String fromID;

    public CallResultAnswer() {
    }

    public CallResult getCallResult() {
        return callResult;
    }

    public void setCallResult(CallResult callResult) {
        this.callResult = callResult;
    }

    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }
}
