package vsp.facade;

import java.io.DataInput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
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
                GET_URL + "/blackboard/quests/1",
                HttpMethod.GET,
                entity,
                String.class
        );
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Quest.class, new QuestDeserializer());
        mapper.registerModule(module);

        Quest quest = mapper.readValue( response.getBody(), Quest.class);
        mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        //Quest quest = mapper.readValue(response.getBody(), Quest.class);

        ResponseEntity<Task> task = restTemplate.exchange(
                GET_URL + quest.getTasks(),
                HttpMethod.GET,
                entity,
                Task.class
        );
/**
 *  Auf der Map nachsehen wo der Raum ist, auf welcher ip er zu finden ist
 */
        ResponseEntity<Room> room = restTemplate.exchange(
                GET_URL + task.getBody().getLocation(),
                HttpMethod.GET,
                entity,
                Room.class
        );
/**
 *  Quest 1 - Aufsuchen des geforderten Raumes mit der ip von der Map und dem Pfad aus den resources vom Task
 *
 *
 *  Hier muss eine Abfrage entstehen ob wir unser ziel erreicht haben oder ob weitere aufgaben zu erledigen sind.
 *  wenn also ein JSON mit {"message":"","next":""} kommt muss dem weiter nachgegangen werden
 */
        ResponseEntity<Achievment> achi = restTemplate.exchange(
                room.getBody().getHost() + task.getBody().getResource(),
                HttpMethod.PUT,
                entity,
                Achievment.class
        );
/**
 *  Absenden des erhaltenen Tokens an /deliveries um die Quest abzuschließen
 *
 *  Hier muss der Body angepasst werden, abhängig davon ob man ein oder mehrere Tokens verschicken muss.
 */
        ResponseEntity<String> end = restTemplate.exchange(
                GET_URL + "deliveries",
                HttpMethod.GET,
                entity,
                String.class
        );

        return end.getBody();
    }
}

