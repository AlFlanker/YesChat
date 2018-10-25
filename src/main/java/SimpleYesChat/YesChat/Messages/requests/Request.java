package SimpleYesChat.YesChat.Messages.requests;


import SimpleYesChat.YesChat.Messages.YesChatMessages;
import SimpleYesChat.YesChat.Messages.answers.Answer;
import SimpleYesChat.YesChat.Messages.answers.StatusContacter;
import SimpleYesChat.YesChat.Messages.notifications.UsersChangeStatusNotification;
import SimpleYesChat.YesChat.UserData.GlobalData;
import SimpleYesChat.YesChat.UserData.UserData;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
@Scope("prototype")
@Component
public  class Request extends YesChatMessages {
    @Autowired
    private GlobalData globalData;

    protected String fromID;

    public Request() {
    }

    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    @Override
    public void execute(WebSocketSession session) {

    }
    @Override
    public void init(YesChatMessages messages) {
        this.fromID = ((Request)messages).fromID;
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
    protected void sendResponse(YesChatMessages message, WebSocketSession session)  {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
        if(session.isOpen()){
            try {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    protected void sendAll() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
        UsersChangeStatusNotification notification;
        for (Map.Entry<WebSocketSession, UserData> entry : globalData.getSessions().entrySet()) {
            if(entry.getValue().isAuth()) {
                for (Map.Entry<WebSocketSession, UserData> entry2 : globalData.getSessions().entrySet()) {
                    if(entry2.getValue().isAuth() && entry2.getKey()!=entry.getKey()) {
                        notification = new UsersChangeStatusNotification();
                        notification.setIdContacter(entry2.getValue().getId());
                        notification.setStatusContacter(StatusContacter.ONLINE);
                        sendResponse(notification,entry.getKey());
                    }
                }
            }

        }
    }

}
