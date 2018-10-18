package SimpleYesChat.YesChat.Messages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.vvvresearch.yescommunicator.network.messages.answers.AllUsersAnswer;
import ru.vvvresearch.yescommunicator.network.messages.answers.AuthAnswer;
import ru.vvvresearch.yescommunicator.network.messages.answers.CallUpAnswer;
import ru.vvvresearch.yescommunicator.network.messages.notifications.UsersChangeStatusNotification;
import ru.vvvresearch.yescommunicator.network.messages.requests.AuthRequest;
import ru.vvvresearch.yescommunicator.network.messages.requests.CallToRequest;
import ru.vvvresearch.yescommunicator.network.messages.requests.GetAllUsersRequest;

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
