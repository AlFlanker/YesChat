package SimpleYesChat.YesChat.Messages.answers;

import SimpleYesChat.YesChat.Messages.YesChatMessages;
import SimpleYesChat.YesChat.UserData.GlobalData;
import SimpleYesChat.YesChat.UserData.UserData;
import SimpleYesChat.YesChat.domain.ChatMessage;
import SimpleYesChat.YesChat.domain.MessageRepo;
import SimpleYesChat.YesChat.domain.User;
import SimpleYesChat.YesChat.domain.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Optional;

@Scope("prototype")
@Component
public class TextMessageAnswer extends Message {
    @Override
    public void init(YesChatMessages messages) {

    }

    @Autowired
    private GlobalData globalData;
    @Autowired
    private MessageRepo messageRepo;
    @Autowired
    private UserRepo userRepo;

    private String fromID;
    private long messageID;

    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    public long getMessageID() {
        return messageID;
    }

    public void setMessageID(long messageID) {
        this.messageID = messageID;
    }

    @Override
    public void execute(WebSocketSession session) {
        answerMessage(this,session);

    }
    protected void answerMessage(TextMessageAnswer request, WebSocketSession session) {
        if(!isAuth(session)){
            return;
        }
        request.setFromID(globalData.getSessions().get(session).getId());
        Optional<Map.Entry<WebSocketSession, UserData>> entry;
        if((entry = globalData.getSessions().entrySet().stream()
                .filter(e->e.getValue().getId().equals(request.getToID()))
                .findFirst()).isPresent()){
                    UserData ud = entry.get().getValue();
                    User user = userRepo.findByUsername(ud.getLogin());
                    if(user!=null){
                        ChatMessage message = messageRepo.findById(messageID);
                        if(message!=null){
                            message.setReceived(true);
                            messageRepo.save(message);
                            sendResponse(request, entry.get().getKey());
                        }

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
