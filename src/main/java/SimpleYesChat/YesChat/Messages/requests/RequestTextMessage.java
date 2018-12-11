package SimpleYesChat.YesChat.Messages.requests;

import SimpleYesChat.YesChat.Messages.answers.CallResult;
import SimpleYesChat.YesChat.Messages.answers.CallUpAnswer;
import SimpleYesChat.YesChat.UserData.GlobalData;
import SimpleYesChat.YesChat.UserData.UserData;
import SimpleYesChat.YesChat.domain.ChatMessage;
import SimpleYesChat.YesChat.domain.MessageRepo;
import SimpleYesChat.YesChat.domain.User;
import SimpleYesChat.YesChat.domain.UserRepo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.Map;
@Scope("prototype")
@Component
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestTextMessage  extends Request {
    @Autowired
    private GlobalData globalData;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private MessageRepo messageRepo;

    private String toID;

    private String message;
    private long messageID;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;

    @Override
    public void execute(WebSocketSession session) {
        sendTo(this,session);
    }

    public String getToID() {
        return toID;
    }

    public void setToID(String toID) {
        this.toID = toID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public long getMessageID() {
        return messageID;
    }

    public void setMessageID(long messageID) {
        this.messageID = messageID;
    }

    public RequestTextMessage() {
    }

    protected void sendTo(RequestTextMessage textMessage, WebSocketSession session){
        CallUpAnswer answer = new CallUpAnswer();
        ChatMessage message = new ChatMessage();
        UserData userData = globalData.getSessions().get(session);
        User owner = userRepo.findByUsername(userData.getLogin());
        message.setLocalDateTime(LocalDateTime.now());
        message.setDest(textMessage.getToID());
        message.setText(textMessage.getMessage());
        message.setOwner(owner);
        message.setReceived(false);
        message = messageRepo.save(message);
        this.setMessageID(message.getId());
        for(Map.Entry<WebSocketSession, UserData> entry:globalData.getSessions().entrySet()) {
            if (entry.getValue().getId().equals(textMessage.getToID())) {
                textMessage.setFromID(globalData.getSessions().get(session).getId());
                    sendResponse(textMessage, entry.getKey());
                    return;
            }
        }
        answer.setResult(CallResult.OFFLINE);
        answer.setDescription("user does't auth");
        answer.setToID(textMessage.getToID());
        sendResponse(answer,session);
        return;

    }

    @Override
    public int hashCode() {
        return this.message.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.equals(obj);
    }
}
