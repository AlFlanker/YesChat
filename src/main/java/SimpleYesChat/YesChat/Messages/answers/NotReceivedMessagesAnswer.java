package SimpleYesChat.YesChat.Messages.answers;

import SimpleYesChat.YesChat.Messages.YesChatMessages;
import SimpleYesChat.YesChat.domain.ChatMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

public class NotReceivedMessagesAnswer extends Message{

    private List<ChatMessage> messages;

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @Override
    public void execute(WebSocketSession session) {

    }

    @Override
    public void init(YesChatMessages messages) {

    }

    public NotReceivedMessagesAnswer() {
    }
}
