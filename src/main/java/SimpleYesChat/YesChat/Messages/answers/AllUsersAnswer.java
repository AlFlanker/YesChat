package SimpleYesChat.YesChat.Messages.answers;


import SimpleYesChat.YesChat.UserData.Contacter;

import java.util.List;

public class AllUsersAnswer extends Message {
    List<Contacter> listOfContacters;


    public AllUsersAnswer() {
    }



    public List<Contacter> getListOfContacters() {
        return listOfContacters;
    }

    public void setListOfContacters(List<Contacter> listOfContacters) {
        this.listOfContacters = listOfContacters;
    }
}
