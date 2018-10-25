package SimpleYesChat.YesChat.Messages.answers;


public class AuthAnswer extends Message  {
    private StatusSS77Auth ss77Auth;
    private String whoAmI;

    public StatusSS77Auth getSs77Auth() {
        return ss77Auth;
    }

    public void setSs77Auth(StatusSS77Auth ss77Auth) {
        this.ss77Auth = ss77Auth;
    }

    public String getWhoAmI() {
        return whoAmI;
    }

    public void setWhoAmI(String whoAmI) {
        this.whoAmI = whoAmI;
    }

}
