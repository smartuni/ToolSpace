package vsp.facade;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import vsp.FinishedFail;
import vsp.Whoami;
import vsp.achievement.Achievment;
import vsp.login.Login;
import vsp.quest.Quest;
import vsp.room.Room;
import vsp.task.Task;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
public class ApplicationFacadeController {

    private Login login = new Login();

    static Logger log =  Logger.getLogger(ApplicationFacadeController.class);

    private static final String GET_URL = "http://172.19.0.7:5000";

    private HttpHeaders header = new HttpHeaders();
    private HttpEntity entity = null;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public void getLogin(RestTemplate restTemplate) throws HTTPException, RestClientResponseException {
        String encode = Base64.getEncoder().encodeToString(("PinkiePie:hamburg").getBytes(StandardCharsets.UTF_8));
        header.add("Authorization", "Basic " + encode);
        entity = new HttpEntity(header);
        ResponseEntity<Login> reslog = restTemplate.exchange(GET_URL + "/login", HttpMethod.GET, entity, Login.class);
        login = reslog.getBody();
    }

    @RequestMapping(value = "/whoami", method = RequestMethod.GET)
    public void getCharName(RestTemplate restTemplate) throws IOException {
        header.add("Authorization", "Token "+ login.getToken());
        entity = new HttpEntity(header);
        ResponseEntity<Whoami> whoami = restTemplate.exchange(GET_URL +"/whoami", HttpMethod.GET, entity, Whoami.class);
        log.info(whoami.getBody().getMessage() +" : "+whoami.getBody().getUser().getName());
    }

    @RequestMapping(value = "/quests", method = RequestMethod.GET)
    public String getQuests(RestTemplate restTemplate) throws IOException {
        header.add("Authorization", "Token "+ login.getToken());
        entity = new HttpEntity(header);
        log.info(GET_URL + "/blackboard/quests/1");
        ResponseEntity<Quest> response = restTemplate.exchange(
                GET_URL + "/blackboard/quests/1",
                HttpMethod.GET,
                entity,
                Quest.class
        );

        ObjectMapper mapper = new ObjectMapper();
        //Quest quest = mapper.readValue( response.getBody(), Quest.class);
        log.info(response.getBody());


        log.info(GET_URL + response.getBody().getTasks().get(0));
        ResponseEntity<Task> task = restTemplate.exchange(
                GET_URL + response.getBody().getTasks().get(0),
                HttpMethod.GET,
                entity,
                Task.class
        );
/**
 *  Auf der Map nachsehen wo der Raum ist, auf welcher ip er zu finden ist
 */
        log.info(GET_URL + task.getBody().getLocation());
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
        log.info(room.getBody().getHost() + task.getBody().getResource());
        ResponseEntity<Achievment> achi = restTemplate.exchange(
                "http://" + room.getBody().getHost() + task.getBody().getResource(),
                HttpMethod.POST,
                entity,
                Achievment.class
        );
/**
 *  Absenden des erhaltenen Tokens an /deliveries um die Quest abzuschließen
 *
 *  Hier muss der Body angepasst werden, abhängig davon ob man ein oder mehrere Tokens verschicken muss.
 */
        String jsonDeliverieToken = "{" +
                "\"tokens\":" +
                "{ \"" + task.getBody().get_links().getSelf() + "\":\"" + achi.getBody().getToken() + "\"}}";
        HttpEntity entity = new HttpEntity(jsonDeliverieToken, header);
        log.info(restTemplate.exchange(
                GET_URL + response.getBody().get_links().getDeliveries(),
                HttpMethod.POST,
                entity,
                String.class
        ));
        ResponseEntity<FinishedFail> end = restTemplate.exchange(
                GET_URL + response.getBody().get_links().getDeliveries(),
                HttpMethod.POST,
                entity,
                FinishedFail.class
        );

        return end.getBody().toString();
    }
}

