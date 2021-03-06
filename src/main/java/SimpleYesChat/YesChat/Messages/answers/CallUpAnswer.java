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
public class CallUpAnswer extends Message  {

    @Autowired
    private GlobalData globalData;

    @Override
    public void init(YesChatMessages messages) {
        this.fromID = ((CallUpAnswer)messages).fromID;
        this.result = ((CallUpAnswer)messages).result;
        this.toID =  ((CallUpAnswer)messages).toID;
    }

    private String fromID;
    private CallResult result;

    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    public CallResult getResult() {
        return result;
    }

    public void setResult(CallResult result) {
        this.result = result;
    }

    @Override
    public void execute(WebSocketSession session) {
        callUp(this,session);

    }
    protected void callUp(CallUpAnswer request, WebSocketSession session) {
        if(!isAuth(session)){
            return;
        }
        Optional<Map.Entry<WebSocketSession, UserData>> entry;
        if((entry = globalData.getSessions().entrySet().stream()
                .filter(e->e.getValue().getId().equals(request.getToID()))
                .findFirst()).isPresent()){
            switch (request.result){
                case HANGUP:
                    globalData.getSessions().get(session).setBusy(false);
                    entry.get().getValue().setBusy(false);
                    sendResponse(request, entry.get().getKey());
                    break;
                case CONNECT:
                    request.setFromID(globalData.getSessions().get(session).getId());
                    sendResponse(request, entry.get().getKey());
                    globalData.getSessions().get(session).setBusy(true);
                    break;
            }
        }
    }

    protected boolean isAuth(WebSocketSession session){
        if(!globalData.getSessions().get(session).isAuth()){
            Answer answer = new Answer();
            answer.setDescription("please sign up");
            sendResponse(answer,session);
            return false;
        }
        return true;
    }

}
