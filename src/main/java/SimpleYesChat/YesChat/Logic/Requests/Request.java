package SimpleYesChat.YesChat.Logic.Requests;

import SimpleYesChat.YesChat.Logic.Requests.Enums.RequestType;


public class Request {
    private RequestType type;

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public Request() {
    }
}
