package SimpleYesChat.YesChat.Logic.Requests;

public class RequestCallTo extends Request{
    private String to;
    private String roomID;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public RequestCallTo() {
    }
}
