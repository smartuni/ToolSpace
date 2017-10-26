package hello.facade;

import hello.UserRepository;
import hello.ToolsRepository;
import hello.User;
import hello.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
class ApplicationFacadeController {




    @Autowired
    private ToolsRepository toolsRepository;

    @Autowired
    private UserRepository userRepository;


    @RequestMapping("/user")
    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    @RequestMapping("/tools")
    public List<Tools> getAllTools(){
        return toolsRepository.findAll();
    }
}