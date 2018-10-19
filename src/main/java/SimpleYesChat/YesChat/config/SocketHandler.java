package SimpleYesChat.YesChat.config;


import SimpleYesChat.YesChat.Messages.YesChatMessages;
import SimpleYesChat.YesChat.Messages.answers.*;
import SimpleYesChat.YesChat.Messages.notifications.UsersChangeStatusNotification;
import SimpleYesChat.YesChat.Messages.requests.AuthRequest;
import SimpleYesChat.YesChat.Messages.requests.CallToRequest;
import SimpleYesChat.YesChat.Messages.requests.GetAllUsersRequest;
import SimpleYesChat.YesChat.Messages.requests.Request;
import SimpleYesChat.YesChat.Services.RestService;
import SimpleYesChat.YesChat.Services.Util;
import SimpleYesChat.YesChat.UserData.UserData;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Component
public class SocketHandler extends AbstractWebSocketHandler {
    private static final String main_url = "https://ss77.ru/cgi-bin/main.cgi";
    private static final String data_url = "https://ss77.ru/cgi-bin/choose_cg_yecomm.cgi";


    Map<WebSocketSession, UserData> sessions = new ConcurrentHashMap<>();


    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(SocketHandler.class);
    @Autowired
    private RestService restService;

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message instanceof TextMessage) {
            handleTextMessage(session, (TextMessage) message);
        }
        else if (message instanceof BinaryMessage) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Binary Messages not supported"));
        }
        else if (message instanceof PongMessage) {
            handlePongMessage(session, (PongMessage) message);
        }
        else {
            throw new IllegalStateException("Unexpected WebSocket message type: " + message);
        }
    }
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        YesChatMessages request = new Request();
        AuthAnswer authAnswer;
        log.info("session -> " + session.getId()  + ","+ "\n"+ "message: " + message.getPayload());
        if(checkForAuth(session, message)){
            try {
                request= RequestDecoder(message);
            } catch (IOException e) {
                authAnswer = new AuthAnswer();
                authAnswer.setWhoAmI(sessions.get(session).getId());
                authAnswer.setDescription("bad data");
                sendResponse(authAnswer,session);
                log.error(e.getMessage());
                return;
            }
            if(request instanceof AuthRequest){
                UserData userData  = new UserData();
                userData.setCookies("");
                userData.setId("");
                userData.setAuth(false);
                sessions.put(session,userData);
                authentication((AuthRequest)request,session);
            }
            if(request instanceof GetAllUsersRequest){
                getAllContacters((GetAllUsersRequest)request,session);
            }
            if(request instanceof CallToRequest){
                callTo((CallToRequest)request,session);
            }
            if(request instanceof CallUpAnswer){
                callUp((CallUpAnswer) request,session);
            }
            
        }
        else {
            try {
                request = RequestDecoder(message);
            } catch (IOException e) {
                authAnswer = new AuthAnswer();
                authAnswer.setWhoAmI(sessions.get(session).getId());
                authAnswer.setDescription("bad data");
                sendResponse(authAnswer,session);
                log.error(e.getMessage());
                return;
            }
            if(request instanceof AuthRequest){
                authentication((AuthRequest)request,session);
            }
            else {
                Answer answer = new Answer();
                answer.setDescription("is not authentication");
                sendResponse(answer,session);
            }
            
        }

    }



    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //the Messages will be broadcasted to all users.
        UserData userData  = new UserData();
        userData.setCookies("");
        userData.setId("");
        userData.setAuth(false);
        sessions.put(session,userData);
        log.info("new Session: " + session.toString() );
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
        log.info("session -> " + session.getId() +"\n"+ " pong message ->" +message.getPayload());
        if(session.isOpen()){
            session.sendMessage(new PingMessage(ByteBuffer.wrap("ping".getBytes())));
            log.info("ConnectionClosed -> " + session.toString() +" " + "detect pong message");
        }

    }


    private boolean checkForAuth(WebSocketSession session,TextMessage message) {
        return sessions.get(session).isAuth();
    }
    private YesChatMessages RequestDecoder(TextMessage message) throws IOException {
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
        YesChatMessages request = objectMapper.readValue(message.getPayload(),YesChatMessages.class);
        return request;

    }
    private void authentication(AuthRequest authRequest, WebSocketSession session) {
        log.info("session -> " + session.getId() + ","+"\n"+ "request auth:-> \n" + "{\n" + "login:" + authRequest.getLogin() + "\n , pass:" + authRequest.getPass() + "\n}");
        String cookies = null;
        AuthAnswer authAnswer ;
        boolean auth = false;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(main_url);
        builder.queryParam("email", authRequest.getLogin());
        builder.queryParam("passw", authRequest.getPass());
        builder.queryParam("INP", "Ввести+данные");
        builder.queryParam("email_check", "");
        try {
            ResponseEntity<String> entity = restService.sendCommand(builder).get();
            if(!Util.checkRes(entity)){
                authAnswer = new AuthAnswer();
                authAnswer.setWhoAmI(sessions.get(session).getId());
                authAnswer.setSs77Auth(StatusSS77Auth.AUTH_DATA_INCORRECT);
                sendResponse(authAnswer,session);
                return;
            }
            HttpHeaders httpHeaders = entity.getHeaders();
            cookies = httpHeaders.getFirst(httpHeaders.SET_COOKIE);
        }
        catch (InterruptedException e) {
            log.error("authentication failed");
            authAnswer = new AuthAnswer();
            authAnswer.setWhoAmI(sessions.get(session).getId());
            authAnswer.setSs77Auth(StatusSS77Auth.AUTH_DATA_INCORRECT);
            sendResponse(authAnswer,session);
            e.printStackTrace();
        } catch (ExecutionException e) {
            log.error("authentication failed");
            authAnswer = new AuthAnswer();
            authAnswer.setWhoAmI(sessions.get(session).getId());
            authAnswer.setSs77Auth(StatusSS77Auth.AUTH_DATA_INCORRECT);
            sendResponse(authAnswer,session);
            e.printStackTrace();
        }
        try {
            if (!StringUtils.isEmpty(cookies)) {
                UserData ud = sessions.get(session);
                ud.setCookies(cookies);
                ResponseEntity<String> tmp = restService.findData(UriComponentsBuilder.fromHttpUrl("https://ss77.ru/cgi-bin/main.cgi"), cookies).get();
                String id = Util.getId(tmp.getBody(), authRequest.getLogin());
                ud.setId(id);
                ud.setAuth(true);
                sessions.put(session, ud);
            }

        } catch (InterruptedException e) {
            log.error("failed to get id");
            authAnswer = new AuthAnswer();
            authAnswer.setWhoAmI(sessions.get(session).getId());
            authAnswer.setSs77Auth(StatusSS77Auth.SS77_NOT_AVAILABLE);
            sendResponse(authAnswer,session);
            e.printStackTrace();
        } catch (ExecutionException e) {
            log.error("failed to get id");
            authAnswer = new AuthAnswer();
            authAnswer.setWhoAmI(sessions.get(session).getId());
            authAnswer.setSs77Auth(StatusSS77Auth.SS77_NOT_AVAILABLE);
            sendResponse(authAnswer,session);
            e.printStackTrace();
        }

        log.info("session -> " + session.getId() + ","+"\n"+"request auth result: cookies = " + cookies);
        log.info("session -> " + session.getId() + ","+"\n"+"request auth result:" +
                "user with " + "login:" + authRequest.getLogin() + " get id->" + sessions.get(session).getId());
        authAnswer = new AuthAnswer();
        authAnswer.setWhoAmI(sessions.get(session).getId());
        authAnswer.setSs77Auth(StatusSS77Auth.OK);
        sendResponse(authAnswer,session);
        sendAll();
    }




    private void getAllContacters(GetAllUsersRequest request,WebSocketSession session){
        AllUsersAnswer allUsersAnswer;
        try {
            ResponseEntity<String> response = restService.findData(UriComponentsBuilder.fromHttpUrl(data_url), sessions.get(session).getCookies()).get();
            String respBody = response.getBody();
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                    Util.getContactersWithOnlineField(
                            Util.getContactList(response.getBody()),sessions))));
            log.info("session -> " + session.getId() +"\n"+ " send list of contact to client->" + session.getRemoteAddress());
        }  catch (UnrecognizedPropertyException | JsonParseException exception ){
            allUsersAnswer = new AllUsersAnswer();
            allUsersAnswer.setDescription("data from ss77 is incorrect");
            if (session.isOpen()) {
                sendResponse(allUsersAnswer,session);
            }

        } catch (InterruptedException | ExecutionException e) {
            allUsersAnswer = new AllUsersAnswer();
            allUsersAnswer.setDescription("WTF?");
            if (session.isOpen()) {
                sendResponse(allUsersAnswer,session);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void callTo( CallToRequest callToRequest,WebSocketSession session){
        for(Map.Entry<WebSocketSession,UserData> entry:sessions.entrySet()){
            if(entry.getValue().getId().equals(callToRequest.getToID())){
                if(entry.getValue().isAuth()){
                    callToRequest.setFromID(sessions.get(session).getId());
                    sendResponse(callToRequest,entry.getKey());
                }
                else{
                    CallUpAnswer answer = new CallUpAnswer();
                    answer.setAnswerCall(false);
                    answer.setDescription("user does't auth");
                    sendResponse(answer,session);
                }
            }
        }
    }

    private void callUp(CallUpAnswer request, WebSocketSession session) {
        Map.Entry<WebSocketSession,UserData> entry = sessions.entrySet().stream()
                .filter(e->e.getValue().getId().equals(request.getDest()))
                .findFirst().get();
        if(entry.getValue().isAuth()){
            sendResponse(request,entry.getKey());
        }
        else{
            CallUpAnswer call = new CallUpAnswer();
            call.setAnswerCall(false);
            call.setFromID(request.getFromID());
            call.setDest(request.getDest());
            sendResponse(call,session);
        }
    }




    private void sendResponse(YesChatMessages message, WebSocketSession session)  {
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
        if(session.isOpen()){
            try {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





    private void sendAll() {
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
        UsersChangeStatusNotification notification = new UsersChangeStatusNotification();
        List<String> list;
        for (Map.Entry<WebSocketSession, UserData> entry : sessions.entrySet()) {
            list = new ArrayList<String>();
            if(entry.getValue().isAuth()) {
                for (Map.Entry<WebSocketSession, UserData> entry2 : sessions.entrySet()) {
                    if(entry2.getValue().isAuth() && entry2.getKey()!=entry.getKey()) {
                        list.add((entry2.getValue().getId()));
                    }
                }
            }

            if (list.size() > 0 && entry.getKey().isOpen()) {
                    notification.setListStatus(list);
//                    entry.getKey().sendMessage(new TextMessage(objectMapper.writeValueAsString(list)));
                    sendResponse(notification,entry.getKey());
            }
        }
    }



}
