package SimpleYesChat.YesChat.UserData;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Scope("singleton")
@Component("globalData")
public class GlobalData {



    private Map<WebSocketSession, UserData> sessions = new ConcurrentHashMap<>();

    public Map<WebSocketSession, UserData> getSessions() {
        return sessions;
    }

    public void setSessions(Map<WebSocketSession, UserData> sessions) {
        this.sessions = sessions;
    }






}
