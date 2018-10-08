package SimpleYesChat.YesChat.Response;

public class UsrAuth {
    private String name;
    private String password;

    public UsrAuth() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UsrAuth{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
