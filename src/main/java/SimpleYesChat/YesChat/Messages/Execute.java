package SimpleYesChat.YesChat.Messages;

import org.springframework.web.socket.WebSocketSession;

public interface Execute {
    void execute(WebSocketSession session);
    void init(YesChatMessages messages);
}
