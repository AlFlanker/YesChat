package SimpleYesChat.YesChat.RequestsAndResponse.Response;

import SimpleYesChat.YesChat.UserData.Contacters;

import java.util.List;

public class ResponseGetAllUsersBody {
    private String result;

    public ResponseGetAllUsersBody() {
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public static class GetBody {
        private List<Contacters> users;

        public GetBody() {
        }

        public List<Contacters> getUsers() {
            return users;
        }

        public void setUsers(List<Contacters> users) {
            this.users = users;
        }
    }
}
