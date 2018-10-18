package SimpleYesChat.YesChat.Messages;

import SimpleYesChat.YesChat.Messages.answers.AllUsersAnswer;
import SimpleYesChat.YesChat.Messages.answers.AuthAnswer;
import SimpleYesChat.YesChat.Messages.answers.CallUpAnswer;
import SimpleYesChat.YesChat.Messages.notifications.UsersChangeStatusNotification;
import SimpleYesChat.YesChat.Messages.requests.AuthRequest;
import SimpleYesChat.YesChat.Messages.requests.CallToRequest;
import SimpleYesChat.YesChat.Messages.requests.GetAllUsersRequest;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AuthRequest.class),
        @JsonSubTypes.Type(value = GetAllUsersRequest.class),
        @JsonSubTypes.Type(value = CallToRequest.class),
        @JsonSubTypes.Type(value = AllUsersAnswer.class),
        @JsonSubTypes.Type(value = CallUpAnswer.class),
        @JsonSubTypes.Type(value = UsersChangeStatusNotification.class),
        @JsonSubTypes.Type(value = AuthAnswer.class)
})
public abstract class YesChatMessages {
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
