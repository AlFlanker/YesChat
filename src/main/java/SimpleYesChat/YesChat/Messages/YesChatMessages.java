package SimpleYesChat.YesChat.Messages;

import SimpleYesChat.YesChat.Messages.answers.*;
import SimpleYesChat.YesChat.Messages.notifications.UsersChangeStatusNotification;
import SimpleYesChat.YesChat.Messages.requests.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AuthRequest.class),
        @JsonSubTypes.Type(value = GetAllUsersRequest.class),
        @JsonSubTypes.Type(value = CallToRequest.class),
        @JsonSubTypes.Type(value = AllUsersAnswer.class),
        @JsonSubTypes.Type(value = CallUpAnswer.class),
        @JsonSubTypes.Type(value = TextMessageAnswer.class),
        @JsonSubTypes.Type(value = UsersChangeStatusNotification.class),
        @JsonSubTypes.Type(value = GetAllNotReceivedMessages.class),
        @JsonSubTypes.Type(value = AuthAnswer.class),
        @JsonSubTypes.Type(value = RequestTextMessage.class),
        @JsonSubTypes.Type(value = NotReceivedMessagesAnswer.class),
        @JsonSubTypes.Type(value = CallResultAnswer.class)
})
public abstract class YesChatMessages implements Execute{
    private String description;

    public YesChatMessages() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
