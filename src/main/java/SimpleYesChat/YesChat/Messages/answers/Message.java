package SimpleYesChat.YesChat.Messages.answers;


import SimpleYesChat.YesChat.Messages.YesChatMessages;

public abstract class Message extends YesChatMessages  {
    protected String toID;

    public String getToID() {
        return toID;
    }

    public void setToID(String toID) {
        this.toID = toID;
    }
}
