package SimpleYesChat.YesChat.RequestsAndResponse.Response;

import SimpleYesChat.YesChat.RequestsAndResponse.CommandType;

public class Response <T> {
    private CommandType EventType;
    private T body;

    public CommandType getEventType() {
        return EventType;
    }

    public void setEventType(CommandType eventType) {
        EventType = eventType;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public Response() {
    }
}
