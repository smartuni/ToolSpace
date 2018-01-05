package hello.facade;

import hello.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
class ApplicationFacadeController {




    @Autowired
    private ToolsRepository toolsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SensorRepository sensorRepository;


    @RequestMapping(value = "/skdata", method = RequestMethod.GET)
    public ResponseEntity<String> getsmartkitchendata(){
    RestTemplate restTemplate = new RestTemplate();
    String fooResourceUrl
            = "http://141.22.28.85/sensor";
    ResponseEntity<String> response
            = restTemplate.getForEntity(fooResourceUrl, String.class);
	return response;
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String getAllUser(){
        List<User> lu =  userRepository.findAll();
	String val = "";
	for(User u : lu){
		val = val + "| " + u.getName() + " | " + u.getLogin() + " |";
		
	}
	return val;
    }

    @RequestMapping(value = "/user", method = RequestMethod.PUT, consumes = {MediaType.TEXT_PLAIN_VALUE})
    @ResponseBody
    public User updateUser(@RequestBody String user_2){
	User n = new User();
	String[] user = user_2.split(":");
        n.setName(user[0]);
        n.setU_lvl(Integer.valueOf(user[1]));
        n.setTime(Integer.valueOf(user[2]));
        userRepository.save(n);
        return n;
    }

    @RequestMapping(value="/login", method = RequestMethod.PUT, consumes = {MediaType.TEXT_PLAIN_VALUE})
    @ResponseBody
    public User logUser(@RequestBody String user_nfc){
	User us = userRepository.findByNfc(user_nfc);
	if (us.getLogin() == 0){
		us.setLogin(1);
	} else {
		us.setLogin(0);
	}
	userRepository.saveAndFlush(us);
	return us;
    }

    @CrossOrigin
    @RequestMapping(value = "/sensor", method = RequestMethod.GET)
    public List<Sensor> getAllWerte(){
        return sensorRepository.findAll();
    }

    @RequestMapping(value = "/sensor", method = RequestMethod.PUT, consumes = {MediaType.TEXT_PLAIN_VALUE}, produces = "text/plain")
    @ResponseBody
    public ResponseEntity createSensorData(@RequestBody String wert){
	try{
		Sensor n = new Sensor();
		n.setWert(Integer.valueOf(wert));
	   	sensorRepository.save(n);
	        return new ResponseEntity(HttpStatus.ACCEPTED);
	}catch(Exception e){
		return new ResponseEntity(HttpStatus.GONE);
	}
    }

    @RequestMapping(value= "/tools", method = RequestMethod.GET)
    public List<Tools> getAllTools(){
        return toolsRepository.findAll();
    }

    @RequestMapping(value= "/tools", method = RequestMethod.PUT, consumes = {MediaType.TEXT_PLAIN_VALUE}, produces = "text/plain")
    @ResponseBody
    public ResponseEntity updateTools(@RequestBody String tool){
        try {
            Tools n = new Tools();
            String[] toolBody = tool.split(":");
            toolsRepository.save(n);
            return new ResponseEntity(HttpStatus.CREATED);
        }catch(Exception e){
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
