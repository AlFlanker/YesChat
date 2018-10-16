package SimpleYesChat.YesChat.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class MessageController {
    private static final Logger log = LoggerFactory.getLogger(MessageController.class);

    @GetMapping("/debug")
    public String list(){
       return "debug_page";
    }



}
