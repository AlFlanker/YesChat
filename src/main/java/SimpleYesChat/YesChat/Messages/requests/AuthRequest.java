package SimpleYesChat.YesChat.Messages.requests;


import SimpleYesChat.YesChat.Messages.YesChatMessages;
import SimpleYesChat.YesChat.Messages.answers.AuthAnswer;
import SimpleYesChat.YesChat.Messages.answers.StatusSS77Auth;
import SimpleYesChat.YesChat.Services.RestService;
import SimpleYesChat.YesChat.Services.ServiceUtil;
import SimpleYesChat.YesChat.Services.UserService;
import SimpleYesChat.YesChat.UserData.GlobalData;
import SimpleYesChat.YesChat.UserData.UserData;
import SimpleYesChat.YesChat.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.concurrent.ExecutionException;
@Scope("prototype")
@Component
public class AuthRequest extends Request {


    private String login;
    private String pass;

    @Value("${url.main}")
    private String main_url;
    @Value("${url.data}")
    private String main_data;

    @Autowired
    private UserService userService;
    @Autowired
    private GlobalData globalData;

    @Autowired
    private RestService restService;

    @Autowired
    private ServiceUtil serviceUtil;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger log = LoggerFactory.getLogger(AuthRequest.class);

    public AuthRequest() {
    }

    public AuthRequest(String login, String pass) {
        this.login = login;
        this.pass = pass;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    @Override
    public void init(YesChatMessages messages) {
        this.login = ((AuthRequest) messages).login;
        this.pass = ((AuthRequest) messages).pass;
    }

    @Override
    public void execute(WebSocketSession session) {
        authentication(session);
    }

    private UriComponentsBuilder getUrl() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(main_url);
        builder.queryParam("email", this.login);
        builder.queryParam("passw", this.pass);
        builder.queryParam("INP", "Ввести+данные");
        builder.queryParam("email_check", "");
        return builder;
    }

    private void authentication(WebSocketSession session) {
        log.info("session -> " + session.getId() + "," + "\n" + "request auth:-> \n" + "{\n" + "login:" + this.login + "\n , pass:" + this.pass + "\n}");
        String cookies = null;
        AuthAnswer authAnswer;
        boolean auth = false;
        UserData userData = new UserData();
        userData.setLogin(this.login);
        userData.setPass(this.pass);
        Map.Entry<WebSocketSession,UserData> entry;
        entry = isUserAlreadyExist(userData);
        if(entry!=null){
            log.warn("\nsession -> " + session.getId() + "\n clean old data -> \n" + entry.getKey().getId());
            globalData.getSessions().remove(entry.getKey());
            globalData.getSessions().put(session,new UserData());

        }
        UriComponentsBuilder builder = getUrl();
        try {
            ResponseEntity<String> entity = restService.sendCommand(builder).get();
            if (!serviceUtil.checkRes(entity)) {
                authAnswer = new AuthAnswer();
                authAnswer.setWhoAmI(globalData.getSessions().get(session).getId());
                authAnswer.setSs77Auth(StatusSS77Auth.AUTH_DATA_INCORRECT);
                sendResponse(authAnswer, session);
                return;
            }
            HttpHeaders httpHeaders = entity.getHeaders();
            cookies = httpHeaders.getFirst(httpHeaders.SET_COOKIE);
        } catch (InterruptedException | ExecutionException e) {
            log.error("authentication failed");
            authAnswer = new AuthAnswer();
            authAnswer.setWhoAmI(globalData.getSessions().get(session).getId());
            authAnswer.setSs77Auth(StatusSS77Auth.AUTH_DATA_INCORRECT);
            sendResponse(authAnswer, session);
            log.error(e.getMessage());
        }
        try {
            if (!StringUtils.isEmpty(cookies)) {
                UserData ud = globalData.getSessions().get(session);
                ud.setCookies(cookies);
                ResponseEntity<String> tmp = restService.findData(UriComponentsBuilder.fromHttpUrl("https://ss77.ru/cgi-bin/main.cgi"), cookies).get();
                String id = serviceUtil.getId(tmp.getBody(), this.login);
                ud.setId(id);
                ud.setLogin(this.login);
                ud.setPass(this.pass);
                ud.setAuth(true);
                User user =userService.addNewUser(ud);
                userService.login(ud);
                globalData.getSessions().put(session, ud);
            }

        } catch (InterruptedException | ExecutionException e) {
            log.error("failed to get id");
            authAnswer = new AuthAnswer();
            authAnswer.setWhoAmI(globalData.getSessions().get(session).getId());
            authAnswer.setSs77Auth(StatusSS77Auth.SS77_NOT_AVAILABLE);
            sendResponse(authAnswer, session);
            log.error(e.getMessage());
        }

        log.info("\nsession -> " + session.getId() + "," + "\n" + "request auth result: cookies = " + cookies);
        log.info("\nsession -> " + session.getId() + "," + "\n" + "request auth result:" +
                "user with " + "login:" + this.login + " get id->" + globalData.getSessions().get(session).getId());
        authAnswer = new AuthAnswer();
        authAnswer.setWhoAmI(globalData.getSessions().get(session).getId());
        authAnswer.setSs77Auth(StatusSS77Auth.OK);
        sendResponse(authAnswer, session);
        sendAll();
    }

    private Map.Entry<WebSocketSession,UserData> isUserAlreadyExist(UserData userData) {

//        for(Map.Entry<WebSocketSession,UserData> m: globalData.getSessions().entrySet()){
//            if(m.getValue().equals(userData)){
//                return m;
//            }
//        }
//        return null;

        return globalData.getSessions().entrySet().stream().filter(e->e.getValue().equals(userData)).findFirst().orElse(null);

    }





}
