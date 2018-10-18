package SimpleYesChat.YesChat.Messages.answers;

public class CallUpAnswer extends Answer {
    private String fromID;
    private boolean isAnswerCall;

    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    public boolean isAnswerCall() {
        return isAnswerCall;
    }

    public void setAnswerCall(boolean answerCall) {
        isAnswerCall = answerCall;
    }
}
