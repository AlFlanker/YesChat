package SimpleYesChat.YesChat.Logic.Responses;

import SimpleYesChat.YesChat.Logic.Responses.Enums.ResponseStatus;
import SimpleYesChat.YesChat.Logic.Responses.Enums.ResponseType;

public class Response {
    private ResponseType type;
    private ResponseStatus status;

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public ResponseType getType() {
        return type;
    }

    public void setType(ResponseType type) {
        this.type = type;
    }

    public Response() {
    }

}
