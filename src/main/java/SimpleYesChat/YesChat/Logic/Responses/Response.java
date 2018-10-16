package SimpleYesChat.YesChat.Logic.Responses;

import SimpleYesChat.YesChat.Logic.Responses.Enums.ResponseType;

public class Response {
    private ResponseType type;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
