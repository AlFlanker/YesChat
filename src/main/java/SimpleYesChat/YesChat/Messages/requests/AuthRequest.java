package SimpleYesChat.YesChat.Messages.requests;

public class AuthRequest extends Request {
    private String login;
    private String pass;

    public AuthRequest() {
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
}
