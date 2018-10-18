package SimpleYesChat.YesChat.UserData;

public class UserData {
    private String id;
    private String cookies;
    private boolean isAuth = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    public boolean isAuth() {
        return isAuth;
    }

    public void setAuth(boolean auth) {
        isAuth = auth;
    }

    public UserData() {
    }
}
