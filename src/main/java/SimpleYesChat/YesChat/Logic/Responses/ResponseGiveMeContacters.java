package SimpleYesChat.YesChat.Logic.Responses;

import SimpleYesChat.YesChat.UserData.Contacters;

import java.util.List;

public class ResponseGiveMeContacters extends Response{
    private List<Contacters> contacters;

    public List<Contacters> getContacters() {
        return contacters;
    }

    public void setContacters(List<Contacters> contacters) {
        this.contacters = contacters;
    }

    public ResponseGiveMeContacters() {
    }
}
