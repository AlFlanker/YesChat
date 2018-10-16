package SimpleYesChat.YesChat.Logic.Responses;

import SimpleYesChat.YesChat.Logic.Responses.Enums.UserAction;

public class ResponseCallTo extends Response {
    private UserAction action;

    public UserAction getAction() {
        return action;
    }

    public void setAction(UserAction action) {
        this.action = action;
    }

    public ResponseCallTo() {
    }
}
