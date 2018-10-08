package SimpleYesChat.YesChat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SocketHandler extends AbstractWebSocketHandler {

    Map<WebSocketSession,String> sessions = new ConcurrentHashMap<>();
    Map<WebSocketSession,String> idBySession = new ConcurrentHashMap<>();
    private final WebSocketUtil webSocketUtil = new WebSocketUtil(sessions,idBySession);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(SocketHandler.class);

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message instanceof TextMessage) {
            handleTextMessage(session, (TextMessage) message);
        }
        else if (message instanceof BinaryMessage) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Binary messages not supported"));
        }
        else if (message instanceof PongMessage) {
            handlePongMessage(session, (PongMessage) message);
        }
        else {
            throw new IllegalStateException("Unexpected WebSocket message type: " + message);
        }
    }
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws IOException {
        log.info("session -> " + session.getId() + ", message: " + message.getPayload());
        webSocketUtil.analizeMessage(session, message);

    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //the messages will be broadcasted to all users.
        sessions.put(session,"");
        log.info("new Session: " + session.toString() );
    }
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.info("TransportError" + " : " + exception.getMessage() + " in " + session.toString());
        super.handleTransportError(session, exception);


    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        log.info("ConnectionClosed -> " + session.toString() + "\n" + "user ID = "+idBySession.get(session)+"\n" + "status:" + status.toString());
        if(idBySession.containsKey(session)){
            idBySession.remove(session);
        }
        if(sessions.containsKey(session)){
            sessions.remove(session);
        }

    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        log.info("session -> " + session.getId() + " pong message ->" +message.getPayload());
        if(session.isOpen()){
            session.sendMessage(new PingMessage(ByteBuffer.wrap("ping".getBytes())));
            log.info("ConnectionClosed -> " + session.toString() +" " + "detect pong message");
        }
//        super.handlePongMessage(session, message);
    }

    @Async
    public CompletableFuture<ResponseEntity<String>> findData(UriComponentsBuilder builder, String cookies) throws InterruptedException {

        return webSocketUtil.findData(builder, cookies);
    }

    @Async
    public CompletableFuture<ResponseEntity<String>>  sendCommand(UriComponentsBuilder builder){
        return webSocketUtil.sendCommand(builder);
    }










}
