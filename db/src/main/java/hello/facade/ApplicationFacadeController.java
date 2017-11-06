package hello.facade;

import hello.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@RestController
class ApplicationFacadeController {




    @Autowired
    private ToolsRepository toolsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SensorRepository sensorRepository;


    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public List<User> getAllUser(){
        return userRepository.findAll();
    }
/*
    @RequestMapping(value = "/user/{name}", method = RequestMethod.GET)
    @ResponseBody
    public Optional<User> getUserByName(@RequestParam("name") String name){
        return userRepository.findByName(name);
    }
*/

    @RequestMapping(value = "/user", method = RequestMethod.PUT, consumes = {MediaType.ALL_VALUE})
    @ResponseBody
    public User updateUser(@RequestBody User user){
       /* User n = new User();
        n.setName(name);
        n.setU_lvl(Integer.valueOf(u_lvl));
        n.setTime(Integer.valueOf(time));*/
        userRepository.save(user);
        return user;
    }

    @RequestMapping(value = "/sensor", method = RequestMethod.GET)
    public List<Sensor> getAllWerte(){
        return sensorRepository.findAll();
    }

    @RequestMapping(value = "/sensor", method = RequestMethod.PUT, consumes = {MediaType.ALL_VALUE})
    @ResponseBody
    public Sensor createSensorData(@RequestBody Sensor wert){
        sensorRepository.save(wert);
        return wert;
    }

    @RequestMapping(value= "/tools", method = RequestMethod.GET)
    public List<Tools> getAllTools(){
        return toolsRepository.findAll();
    }

    @RequestMapping(value= "/tools", method = RequestMethod.PUT, consumes = {MediaType.ALL_VALUE})
    @ResponseBody
    public Tools updateTools(@RequestBody Tools tool){
	toolsRepository.save(tool);
	return tool;
    }
}
