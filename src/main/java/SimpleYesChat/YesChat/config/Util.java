package SimpleYesChat.YesChat.config;

import SimpleYesChat.YesChat.UserData.Contacter;
import SimpleYesChat.YesChat.UserData.UserData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Util {


    public static String getId(String list, String email) {
        int pos = list.indexOf("<span>" + email + " .... ID");
        if (pos > -1) {
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

    public static List<Contacter> getContactersWithOnlineField(List<Contacter> list,Map<WebSocketSession, UserData> session) {
        for (Map.Entry<WebSocketSession, UserData> entry : session.entrySet()) {
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

    public static List<Contacter> getContactList(String data) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Contacter> contacters = new ArrayList<Contacter>();
        String fieldName;
        try {
            JsonNode rootNode = objectMapper.readTree(data);
            JsonNode cont = rootNode.path("contacters");
            Iterator<String> it = cont.fieldNames();
            while (it.hasNext()) {
                fieldName = it.next();
                Contacter contacter = objectMapper.readValue(cont.get(fieldName).toString(), Contacter.class);
                contacter.setId(fieldName);
                contacters.add(contacter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contacters;
    }
}