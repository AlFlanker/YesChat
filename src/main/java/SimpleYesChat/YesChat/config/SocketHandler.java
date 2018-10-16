package SimpleYesChat.YesChat.config;

import SimpleYesChat.YesChat.RequestsAndResponse.Requests.RequestAuth;
import SimpleYesChat.YesChat.RequestsAndResponse.Requests.RequestCall;
import SimpleYesChat.YesChat.RequestsAndResponse.Response.ResponseID;
import SimpleYesChat.YesChat.UserData.Contacters;
import SimpleYesChat.YesChat.UserData.UserData;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
public class SocketHandler extends AbstractWebSocketHandler {
    private static final String main_url = "https://ss77.ru/cgi-bin/main.cgi";
    private static final String data_url = "https://ss77.ru/cgi-bin/choose_cg_yecomm.cgi";

    Map<WebSocketSession, UserData> sessions = new ConcurrentHashMap<>();
    private WebSocketUtil webSocketUtil = new WebSocketUtil(sessions);

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
        UserData userData  = new UserData();
        userData.setCookies("");
        userData.setId("");
        sessions.put(session,userData);
        log.info("new Session: " + session.toString() );
    }
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.info("TransportError" + " : " + exception.getMessage() + " in " + session.toString());
        super.handleTransportError(session, exception);


    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        log.info("ConnectionClosed -> " + session.toString() + "\n" + "user ID = "+ sessions.get(session).getId()+"\n" + "status:" + status.toString());
        if(sessions.containsKey(session)){
            sessions.remove(session);
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

    public void analizeMessage(WebSocketSession session, TextMessage message) throws IOException {
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
        String res = message.getPayload();
        if (StringUtils.isEmpty(sessions.get(session).getCookies())) {
            try {
                RequestAuth ra = objectMapper.readValue(res, RequestAuth.class);
                log.info("session -> " + session.getId() + ", request auth:-> \n" + "login:" + ra.getLogin() + " , pass:" + ra.getPass());
                String result = isAuth(ra, session);
                log.info("session -> " + session.getId() + ", request auth result: cookies = " + result);
                log.info("session -> " + session.getId() + ", request auth result:" +
                        "user with " + "login:" + ra.getLogin() + " get id->" + sessions.get(session).getId());
                if (!StringUtils.isEmpty(result)) {
                    UserData ud = sessions.get(session);
                    ud.setCookies(result);
                    sessions.put(session, ud);
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage("{message:\"success\",id:\"" + sessions.get(session).getId() + "\"}"));
                        log.info("session -> " + session.getId() + " send message to client " + session.getRemoteAddress() + '\n' +
                                "message : " + "{message:\"success\",id:\"" + sessions.get(session).getId() + "\"}");
                        sendAll(session);
                    }
                } else {

                    sessions.put(session, null);
                    if (session.isOpen()) session.sendMessage(new TextMessage("{message:\"data isn't correct\"}"));
                    log.info("session -> " + session.getId() + " send message to client " + session.getRemoteAddress() + '\n' +
                            "message : " + "{message:\"data isn't correct\"}");
                }
            } catch (JsonParseException e) {
                log.info(e.getMessage());
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage("{message:\"error\""));
                    log.info("session -> " + session.getId() + " send message to client " + session.getRemoteAddress() + '\n' +
                            "message : " + "{message:\"error\"");
                    sessions.remove(session);
                    session.close(CloseStatus.BAD_DATA);
                }
            } catch (NullPointerException e) {
                log.info(e.getMessage());
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage("{message:\"error\""));
                    log.info("session -> " + session.getId() + " send message to client " + session.getRemoteAddress() + '\n' +
                            "{message:\"error\"");
                    sessions.remove(session);
                    session.close(CloseStatus.BAD_DATA);
                }

            }
        } else if (res.contains("roomId:")) {
            RequestCall ra = objectMapper.readValue(res, RequestCall.class);

            if (sessions.entrySet().stream().filter(entry->entry.getValue().getId().equals(ra.getTo())).findFirst().isPresent()) {
                sessions.entrySet().stream()
                        .filter(entry -> Objects.equals(entry.getValue().getId(), ra.getTo()))
                        .findFirst()
                        .map(Map.Entry::getKey).get()
                        .sendMessage(new TextMessage(objectMapper.writeValueAsBytes(ra)));
                log.info("session -> " + session.getId() + " get message with RoomID from client->" + session.getRemoteAddress());

            } else {
                session.sendMessage(new TextMessage("{message:\"user offline\""));
                log.info("session -> " + session.getId() + " send message to client " + session.getRemoteAddress() + '\n' +
                        "{message:\"user offline\"");
            }
        } else if (res.contains("type:")) {
            try {
                ResponseEntity<String> response = findData(UriComponentsBuilder.fromHttpUrl(data_url), sessions.get(session).getCookies()).get();
                String r = response.getBody();
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(getContactersWithOnlineField(getContactList(response.getBody()), session))));
                log.info("session -> " + session.getId() + " send list of contact to client->" + session.getRemoteAddress());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public String isAuth(RequestAuth requestAuth, WebSocketSession wss) {
        String cookies;
        boolean auth = false;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(main_url);
        builder.queryParam("email", requestAuth.getLogin());
        builder.queryParam("passw", requestAuth.getPass());
        builder.queryParam("INP", "Ввести+данные");
        builder.queryParam("email_check", "");
        try {
            ResponseEntity<String> entity = sendCommand(builder).get();
            HttpHeaders httpHeaders = entity.getHeaders();
            cookies = httpHeaders.getFirst(httpHeaders.SET_COOKIE);
            if (!StringUtils.isEmpty(cookies)) {
                ResponseEntity<String> tmp = findData(UriComponentsBuilder.fromHttpUrl("https://ss77.ru/cgi-bin/main.cgi"), cookies).get();
                String id = getId(tmp.getBody(), requestAuth.getLogin());
                UserData ud = sessions.get(wss);
                ud.setId(id);
                sessions.put(wss, ud);
            }
            return cookies;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "";
    }

    void sendAll(WebSocketSession session) {
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
        List<ResponseID> list;
        for (Map.Entry<WebSocketSession, UserData> entry : sessions.entrySet()) {
            list = new ArrayList<ResponseID>();
            if(!StringUtils.isEmpty(entry.getValue().getCookies())) {
                for (Map.Entry<WebSocketSession, UserData> entry2 : sessions.entrySet()) {
                    list.add(new ResponseID(entry2.getValue().getId()));
                }
            }

            if (list.size() > 0 && entry.getKey().isOpen()) {
                try {
                    entry.getKey().sendMessage(new TextMessage(objectMapper.writeValueAsString(list)));
                    log.info("session -> " + session.getId() + " send user with id " + sessions.get(session).getId() + " list of readyToSpeak users\n" + "payload: " + objectMapper.writeValueAsString(list));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    String getId(String list, String email) {
        int pos = list.indexOf("<span>" + email + " .... ID");
        if(pos>-1) {
            String srh = list.substring(pos);
            String tmp = srh.substring(srh.indexOf("ID ") + 3, srh.indexOf("</"));

            if (StringUtils.isEmpty(tmp)) {
                return "";
            } else {
                return tmp;
            }
        }
        return "";
    }

    List<Contacters> getContactersWithOnlineField(List<Contacters> list, WebSocketSession session) {
        for (Map.Entry<WebSocketSession, UserData> entry : sessions.entrySet()) {
            list.stream()
                    .filter(a -> a.getId().equals(entry.getValue().getId()))
                    .map(elem -> {
                        elem.setOnline(true);
                        return elem;
                    })
                    .collect(Collectors.toList());
        }
        return list;
    }

    List<Contacters> getContactList(String data) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Contacters> contacters = new ArrayList<Contacters>();
        String fieldName;
        try {
            JsonNode rootNode = objectMapper.readTree(data);
            JsonNode cont = rootNode.path("contacters");
            Iterator<String> it = cont.fieldNames();
            while (it.hasNext()) {
                fieldName = it.next();
                Contacters contacter = objectMapper.readValue(cont.get(fieldName).toString(), Contacters.class);
                contacter.setId(fieldName);
                contacters.add(contacter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contacters;
    }








}
