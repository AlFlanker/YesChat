package SimpleYesChat.YesChat.Messages.requests;

import SimpleYesChat.YesChat.Messages.YesChatMessages;
import SimpleYesChat.YesChat.Messages.answers.NotReceivedMessagesAnswer;
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

import java.util.List;

@Scope("prototype")
@Component
public class GetAllNotReceivedMessages  extends Request {
    @Autowired
    private GlobalData globalData;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private MessageRepo messageRepo;

    @Override
    public void execute(WebSocketSession session) {
        NotReceivedMessagesAnswer answer = new NotReceivedMessagesAnswer();
        UserData ud = globalData.getSessions().get(session);
        if (ud != null) {
            User user = userRepo.findByUsername(ud.getLogin());
            if (user != null) {
                List<ChatMessage> messages = messageRepo.findByIsReceived(false);
                if(messages!=null) {
                    answer.setMessages(messages);
                    sendResponse(answer, session);
                    for(ChatMessage message:messages){
                        message.setReceived(true);
                    }
                    messageRepo.saveAll(messages);
                }
            }

        }
    }

    @Override
    public void init(YesChatMessages messages) {

    }
}
