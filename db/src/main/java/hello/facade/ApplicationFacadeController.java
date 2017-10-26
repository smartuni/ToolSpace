package hello.facade;

import hello.UserRepository;
import hello.ToolsRepository;
import hello.User;
import hello.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@RestController
class ApplicationFacadeController {




    @Autowired
    private ToolsRepository toolsRepository;

    @Autowired
    private UserRepository userRepository;


    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    @RequestMapping(value = "/user/{name}", method = RequestMethod.GET)
    @ResponseBody
    public Optional<User> getUserByName(@RequestParam("name") String name){
        return userRepository.findByName(name);
    }



    @RequestMapping(value = "/user", method = RequestMethod.PUT)
    @ResponseBody
    public User updateUser(@RequestParam String name, @RequestParam String u_lvl, @RequestParam String time){
        User n = new User();
        n.setName(name);
        n.setU_lvl(u_lvl);
        n.setTime(time);
        userRepository.save(n);
        return n;
    }


    @RequestMapping("/tools")
    public List<Tools> getAllTools(){
        return toolsRepository.findAll();
    }
}