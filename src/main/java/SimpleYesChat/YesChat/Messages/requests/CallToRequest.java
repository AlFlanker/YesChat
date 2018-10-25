package SimpleYesChat.YesChat.Messages.requests;


public class CallToRequest extends Request {
    private String toID;
    private String roomID;

    public CallToRequest() {
    }

    public CallToRequest(String fromID, String toID, String roomID) {
        this.fromID = fromID;
        this.toID = toID;
        this.roomID = roomID;
    }



    public String getToID() {
        return toID;
    }

    public void setToID(String toID) {
        this.toID = toID;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }
}
