package SimpleYesChat.YesChat.Messages.requests;

import ru.vvvresearch.yescommunicator.network.messages.YesChatMessages;

public abstract class Request extends YesChatMessages {
    private String fromID;

    public Request() {
    }

    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }
}
