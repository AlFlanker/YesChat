package SimpleYesChat.YesChat.Controller;

import SimpleYesChat.YesChat.Services.RestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class BaseController {
    private static final Logger log = LoggerFactory.getLogger(BaseController.class);
    @Autowired
    private RestService service;
    @GetMapping("/debug")
    public String list(){
       return "debug_page";
    }
//    @GetMapping("/")
//    public String main(){
//        return "index";
//    }


}
