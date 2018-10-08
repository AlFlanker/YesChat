package SimpleYesChat.YesChat.Response;

import org.springframework.http.ResponseEntity;

public class ResponseFromSS77 {

    private ResponseEntity<String> responseEntity;
    private String code;

    public ResponseFromSS77() {

    }

    public ResponseEntity<String> getResponseEntity() {
        return responseEntity;
    }

    public void setResponseEntity(ResponseEntity<String> responseEntity) {
        this.responseEntity = responseEntity;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
