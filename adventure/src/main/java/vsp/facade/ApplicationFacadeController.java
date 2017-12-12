package vsp.facade;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import vsp.*;

@RestController
public class ApplicationFacadeController {

    private Login login = new Login();

    static Logger log =  Logger.getLogger(ApplicationFacadeController.class);

    private static final String GET_URL = "http://172.19.0.7:5000";

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public void getLogin(RestTemplate restTemplate) throws IOException {
        HttpHeaders httpHeaders = new HttpHeaders();
        String encode = Base64.getEncoder().encodeToString(("PinkiePie:hamburg").getBytes(StandardCharsets.UTF_8));
        httpHeaders.add("Authorization", "Basic " + encode);
        HttpEntity entity = new HttpEntity(httpHeaders);
        ResponseEntity<Login> reslog = restTemplate.exchange(GET_URL + "/login", HttpMethod.GET, entity, Login.class);
        login = reslog.getBody();
    }

    @RequestMapping(value = "/whoami", method = RequestMethod.GET)
    public void getCharName(RestTemplate restTemplate) throws IOException {
        HttpHeaders header = new HttpHeaders();
        HttpEntity entity = new HttpEntity(header);
        header.add("Authorization", "Token "+ login.getToken());
        entity = new HttpEntity(header);
        ResponseEntity<Whoami> whoami = restTemplate.exchange(GET_URL +"/whoami", HttpMethod.GET, entity, Whoami.class);
        log.info(whoami.getBody().getMessage() +" : "+whoami.getBody().getUser().getName());
    }

    @RequestMapping(value = "/quests", method = RequestMethod.GET)
    public String getQuests(RestTemplate restTemplate) throws IOException {
        HttpHeaders header = new HttpHeaders();
        header.add("Authorization", "Token "+ login.getToken());
        HttpEntity entity = new HttpEntity(header);
        ResponseEntity<String> response = restTemplate.exchange(
                GET_URL + "/blackboard/quests",
                HttpMethod.GET,
                entity,
                String.class
        );
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Quest[] quest = mapper.readValue(response.getBody(), Quest[].class);
        return quest.toString();
    }
}

