package SimpleYesChat.YesChat.Messages.answers;


import SimpleYesChat.YesChat.Messages.YesChatMessages;
import SimpleYesChat.YesChat.UserData.Contacter;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

public class AllUsersAnswer extends Message {
    @Override
    public void init(YesChatMessages messages) {

    }

    @Override
    public void execute(WebSocketSession session) {

    }

    List<Contacter> listOfContacters;


    public AllUsersAnswer() {
    }



    public List<Contacter> getListOfContacters() {
        return listOfContacters;
    }

    public void setListOfContacters(List<Contacter> listOfContacters) {
        this.listOfContacters = listOfContacters;
    }
}
