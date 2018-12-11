package SimpleYesChat.YesChat.Messages.requests;


import SimpleYesChat.YesChat.Messages.YesChatMessages;
import SimpleYesChat.YesChat.Messages.answers.AllUsersAnswer;
import SimpleYesChat.YesChat.Services.RestService;
import SimpleYesChat.YesChat.Services.ServiceUtil;
import SimpleYesChat.YesChat.UserData.Contacter;
import SimpleYesChat.YesChat.UserData.GlobalData;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
@Scope("prototype")
@Component
public class GetAllUsersRequest extends Request  {

    @Value("${url.main}")
    private String main_url;
    @Value("${url.data}")
    private String main_data;
    private static final Logger log = LoggerFactory.getLogger(GetAllUsersRequest.class);
    @Autowired
    private RestService restService;
    @Autowired
    private GlobalData globalData;
    @Autowired
    private ServiceUtil serviceUtil;


    @Override
    public void execute(WebSocketSession session) {
//        Authentication authentication = getAuthentication();
//        User user = (User) getAuthentication().getPrincipal();
//        log.info(user.toString());
       if(isAuth(session)) {
           getAllContacters(session);
       }
    }

    @Override
    public void init(YesChatMessages messages) {
        super.init(messages);
    }

    private void getAllContacters(WebSocketSession session){
        AllUsersAnswer allUsersAnswer;
        ObjectMapper objectMapper = new ObjectMapper();
        List<Contacter> list ;
        try {
            ResponseEntity<String> response = restService.findData(UriComponentsBuilder.fromHttpUrl(main_data), globalData.getSessions().get(session).getCookies()).get();
            String respBody = response.getBody();
            allUsersAnswer = new AllUsersAnswer();
            list=serviceUtil.getContactList(response.getBody());
            allUsersAnswer.setListOfContacters(serviceUtil.getContactersWithOnlineField(list));
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(allUsersAnswer)));
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
}
