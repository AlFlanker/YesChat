package SimpleYesChat.YesChat.Messages.requests;


import SimpleYesChat.YesChat.Messages.IAuthenticationFacade;
import SimpleYesChat.YesChat.Messages.YesChatMessages;
import SimpleYesChat.YesChat.Messages.answers.CallResult;
import SimpleYesChat.YesChat.Messages.answers.CallUpAnswer;
import SimpleYesChat.YesChat.UserData.GlobalData;
import SimpleYesChat.YesChat.UserData.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
@Scope("prototype")
@Component
public class CallToRequest extends Request implements IAuthenticationFacade {
    @Autowired
    private GlobalData globalData;

    @Override
    public void execute(WebSocketSession session) {

        if(isAuth(session)) {
            callTo(this, session);
        }
    }


    @Override
    public void init(YesChatMessages messages) {
        this.roomID = ((CallToRequest) messages).roomID;
        this.toID = ((CallToRequest) messages).toID;
        this.fromID = ((CallToRequest) messages).fromID;
    }

    private String toID;
    private String roomID;

    public CallToRequest() {
    }

    public CallToRequest(String fromID, String toID, String roomID) {
        this.fromID = fromID;
        this.toID = toID;
        this.roomID = roomID;
    }



    public String getToID() {
        return toID;
    }

    public void setToID(String toID) {
        this.toID = toID;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    protected void callTo( CallToRequest callToRequest,WebSocketSession session){
        CallUpAnswer answer = new CallUpAnswer();
        for(Map.Entry<WebSocketSession, UserData> entry:globalData.getSessions().entrySet()) {
            if (entry.getValue().getId().equals(callToRequest.getToID())) {
                if (!entry.getValue().isBusy()) {
                    callToRequest.setFromID(globalData.getSessions().get(session).getId());
                    sendResponse(callToRequest, entry.getKey());
                    globalData.getSessions().get(session).setBusy(true);
                    return;
                }
                else{
                    answer.setResult(CallResult.BUSY);
                    answer.setDescription("user is busy");
                    answer.setToID(callToRequest.getToID());
                    sendResponse(answer,session);
                    return;
                }
            }
        }

                answer.setResult(CallResult.OFFLINE);
                answer.setDescription("user does't auth");
                answer.setToID(callToRequest.getToID());
                sendResponse(answer,session);
                return;

        }

    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
