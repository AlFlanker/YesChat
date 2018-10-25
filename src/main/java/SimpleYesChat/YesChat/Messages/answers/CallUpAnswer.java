package SimpleYesChat.YesChat.Messages.answers;



public class CallUpAnswer extends Message  {
    private String fromID;
    private StatusContacter statusContacter;

    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    public StatusContacter getStatusContacter() {
        return statusContacter;
    }

    public void setStatusContacter(StatusContacter statusContacter) {
        this.statusContacter = statusContacter;
    }


}
