package SimpleYesChat.YesChat.config;

import SimpleYesChat.YesChat.RequestsAndResponse.Requests.RequestAuth;
import SimpleYesChat.YesChat.RequestsAndResponse.Requests.RequestCall;
import SimpleYesChat.YesChat.RequestsAndResponse.Response.ResponseID;
import SimpleYesChat.YesChat.UserData.Contacters;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class WebSocketUtil {
    
    private static final Logger log = LoggerFactory.getLogger(WebSocketUtil.class);
    Map<WebSocketSession,String> sessions = new ConcurrentHashMap<>();
    Map<WebSocketSession,String> idBySession = new ConcurrentHashMap<>();
    private String main_url = "https://ss77.ru/cgi-bin/main.cgi";
    private String data_url = "https://ss77.ru/cgi-bin/choose_cg_yecomm.cgi";
    public WebSocketUtil(Map<WebSocketSession, String> sessions, Map<WebSocketSession, String> idBySession) {
        this.sessions = sessions;
        this.idBySession = idBySession;
    }

    private RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    public void analizeMessage(WebSocketSession session, TextMessage message) throws IOException {
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
        String res = message.getPayload();
        if (StringUtils.isEmpty(sessions.get(session))) {
            try {
                RequestAuth ra = objectMapper.readValue(res, RequestAuth.class);
                log.info("session -> " + session.getId() + ", request auth:-> \n" + "login:" + ra.getLogin() + " , pass:" + ra.getPass());
                String result = isAuth(ra, session);
                log.info("session -> " + session.getId() + ", request auth result: cookies = " + result);
                log.info("session -> " + session.getId() + ", request auth result:" +
                        "user with " + "login:" + ra.getLogin() + " get id->" + idBySession.get(session));
                if (!StringUtils.isEmpty(result)) {
                    sessions.put(session, result);
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage("{message:\"success\",id:\"" + idBySession.get(session) + "\"}"));
                        log.info("session -> " + session.getId() + " send message to client " + session.getRemoteAddress() + '\n' +
                                "message : " + "{message:\"success\",id:\"" + idBySession.get(session) + "\"}");
                        sendAll(session);
                    }
                } else {
                    sessions.put(session, "");
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
            if (idBySession.containsValue(ra.getTo())) {
                idBySession.entrySet().stream()
                        .filter(entry -> Objects.equals(entry.getValue(), ra.getTo()))
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
                ResponseEntity<String> response = findData(UriComponentsBuilder.fromHttpUrl(data_url), sessions.get(session)).get();
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
                ResponseEntity<String> tmp = findData(UriComponentsBuilder.fromHttpUrl("https://ss77.ru/cgi-bin/choose_cg1.cgi"), cookies).get();
                String id = getId(tmp.getBody(), requestAuth.getLogin());
                idBySession.put(wss, id);
            }
            return cookies;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Async
    public CompletableFuture<ResponseEntity<String>> findData(UriComponentsBuilder builder, String cookies) throws InterruptedException {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Cookie", cookies);
        HttpEntity requstEntry = new HttpEntity(null, httpHeaders);
        ResponseEntity<String> entry = restTemplate.exchange(builder.build().encode().toUri(),
                HttpMethod.GET,
                requstEntry,
                String.class);
        return CompletableFuture.completedFuture(entry);
    }

    @Async
    public CompletableFuture<ResponseEntity<String>> sendCommand(UriComponentsBuilder builder) {
        log.info("sendCommand");
        ResponseEntity<String> entry = restTemplate.getForEntity(builder.build().encode().toUri(), String.class);
        return CompletableFuture.completedFuture(entry);
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

    String getId(String list, String email) {
        int pos = list.indexOf("<span>" + email + " .... ID");
        String srh = list.substring(pos);
        String tmp = srh.substring(srh.indexOf("ID ") + 3, srh.indexOf("</"));
        if (StringUtils.isEmpty(tmp)) {
            return "";
        } else {
            return tmp;
        }
    }

    List<Contacters> getContactersWithOnlineField(List<Contacters> list, WebSocketSession session) {
        for (Map.Entry<WebSocketSession, String> entry : idBySession.entrySet()) {
            list.stream()
                    .filter(a -> a.getId().equals(entry.getValue()))
                    .map(elem -> {
                        elem.setOnline(true);
                        return elem;
                    })
                    .collect(Collectors.toList());
        }
        return list;
    }

    void sendAll(WebSocketSession session) {
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
        List<ResponseID> list;
        for (Map.Entry<WebSocketSession, String> entry : sessions.entrySet()) {
            list = new ArrayList<ResponseID>();
            if(!StringUtils.isEmpty(entry.getValue())) {
                for (Map.Entry<WebSocketSession, String> entry2 : idBySession.entrySet()) {
                    list.add(new ResponseID(entry2.getValue()));
                }
            }

            if (list.size() > 0 && entry.getKey().isOpen()) {
                try {
                    entry.getKey().sendMessage(new TextMessage(objectMapper.writeValueAsString(list)));
                    log.info("session -> " + session.getId() + " send user with id " + idBySession.get(session) + " list of readyToSpeak users\n" + "payload: " + objectMapper.writeValueAsString(list));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    List<ResponseID> getRespID(String currentID) {
        List<ResponseID> list = new ArrayList<ResponseID>();
        ResponseID resId;
        for (Map.Entry<WebSocketSession, String> entry : idBySession.entrySet()) {
            if (!(entry.getValue().equals(currentID))) {
                resId = new ResponseID();
                resId.setId(entry.getValue());
                list.add(resId);
            }
        }
        return list;
    }
}