package SimpleYesChat.YesChat.Messages.requests;


import SimpleYesChat.YesChat.Messages.YesChatMessages;

public  class Request extends YesChatMessages {
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
