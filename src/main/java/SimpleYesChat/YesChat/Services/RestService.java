package SimpleYesChat.YesChat.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.concurrent.CompletableFuture;

@Service
public class RestService {
    private static final Logger logger = LoggerFactory.getLogger(RestService.class);

    private final RestTemplate restTemplate;

    public RestService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Async
    public CompletableFuture<ResponseEntity<String>> findData(UriComponentsBuilder builder, String cookies) throws InterruptedException {
        logger.info("findData");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Cookie", cookies);
        HttpEntity requstEntry = new HttpEntity(null, httpHeaders);
        ResponseEntity<String> entry = restTemplate.exchange(builder.build().encode().toUri(),
                HttpMethod.GET,
                requstEntry,
                String.class);
        return CompletableFuture.completedFuture(entry);
    }

    @Async
    public CompletableFuture<ResponseEntity<String>>  sendCommand(UriComponentsBuilder builder){
        logger.info("sendCommand");
        ResponseEntity<String> entry = restTemplate.getForEntity(builder.build().encode().toUri(), String.class);
        return CompletableFuture.completedFuture(entry);
    }
}
