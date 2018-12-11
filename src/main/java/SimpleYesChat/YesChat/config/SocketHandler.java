package SimpleYesChat.YesChat.config;


import SimpleYesChat.YesChat.Messages.Execute;
import SimpleYesChat.YesChat.Messages.YesChatMessages;
import SimpleYesChat.YesChat.Messages.answers.Answer;
import SimpleYesChat.YesChat.Messages.answers.StatusContacter;
import SimpleYesChat.YesChat.Messages.notifications.UsersChangeStatusNotification;
import SimpleYesChat.YesChat.UserData.GlobalData;
import SimpleYesChat.YesChat.UserData.UserData;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

/*потестировать*/
@Scope("session")
@Component
public class SocketHandler extends AbstractWebSocketHandler {
    private GlobalData globalData;
    private GenericWebApplicationContext context;
    @Autowired
     private SessionRegistry sessionRegistry;

    @Autowired
    public void setGlobalData(GlobalData globalData) {
        this.globalData = globalData;
    }
    @Autowired
    public void setContext(GenericWebApplicationContext context) {
        this.context = context;
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger log = LoggerFactory.getLogger(SocketHandler.class);

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message instanceof TextMessage) {
            handleTextMessage(session, (TextMessage) message);
        } else if (message instanceof BinaryMessage) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Binary Messages not supported"));
        } else if (message instanceof PongMessage) {
            handlePongMessage(session, (PongMessage) message);
        } else {
            throw new IllegalStateException("Unexpected WebSocket message type: " + message);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)  {
        YesChatMessages request;
        long time;
//        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> log.error(e.getMessage()));
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
//        objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
//        RequestTextMessage t = new RequestTextMessage();
//        t.setDateTime(LocalDateTime.now());
//        try {
//            String y = objectMapper.writeValueAsString(t);
//            log.info(y);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }

        try {
            time = System.currentTimeMillis();
            request = RequestDecoder(message);
            Execute ex = context.getBean(request.getClass());
            BeanUtils.copyProperties(request,ex);
            log.info("\nNew Request {\n type:" + request.getClass().getSimpleName() + ",\n" + "data: \n" + message.getPayload() + "\n}");
//            ex.init((request));
            ex.execute(session);
            log.warn("\nSession: " + session.toString() + "\nBeans hashCode:" + String.valueOf(ex.hashCode()));
            time = System.currentTimeMillis() - time;
            log.info("\nSession: " + session.toString() + "\ntime for execute - " +request.getClass().getSimpleName() + " "+ time + " ms");
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        UserData userData = new UserData();

        userData.setCookies("");
        userData.setId("");
        userData.setAuth(false);
        globalData.getSessions().put(session, userData);
        log.info("new Session: " + session.toString());
        Answer answer = new Answer();
        answer.setDescription(session.getHandshakeHeaders().toString());
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(answer)));

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.info("TransportError" + " : " + exception.getMessage() + " in " + session.toString());
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {

        log.info("ConnectionClosed -> " + session.toString() + "\n" + "user ID = " + globalData.getSessions().get(session).getId() + "\n" + "status:" + status.toString());
        if (globalData.getSessions().containsKey(session)) {
            globalData.getSessions().get(session).setAuth(false);
        }
        updateStatus();
        globalData.getSessions().remove(session);
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        log.info("session -> " + session.getId() + "\n" + " pong message ->" + message.getPayload());
        if (session.isOpen()) {
            session.sendMessage(new PingMessage(ByteBuffer.wrap("ping".getBytes())));
            log.info("ConnectionClosed -> " + session.toString() + " " + "detect pong message");
        }

    }

    private YesChatMessages RequestDecoder(TextMessage message) throws IOException {
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
        YesChatMessages request = objectMapper.readValue(message.getPayload(), YesChatMessages.class);
        return request;
    }

    protected void updateStatus() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
        UsersChangeStatusNotification notification;
        for (Map.Entry<WebSocketSession, UserData> entry : globalData.getSessions().entrySet()) {
            if (entry.getValue().isAuth()) {
                for (Map.Entry<WebSocketSession, UserData> entry2 : globalData.getSessions().entrySet()) {
                    if ((!StringUtils.isEmpty(entry2.getValue().getId())) && (entry2.getKey() != entry.getKey())) {
                        notification = new UsersChangeStatusNotification();
                        notification.setIdContacter(entry2.getValue().getId());
                        notification.setStatusContacter(entry2.getValue().isAuth()?StatusContacter.ONLINE:StatusContacter.OFFLINE);
                        sendResponse(notification, entry.getKey());
                    }
                }
            }

        }
    }

    protected void sendResponse(YesChatMessages message, WebSocketSession session) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
        if (session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

}
