package hello;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping(path="/api")
public class TestController {
    @GetMapping(path = "/hello-world")
    public String helloWorld(){
        return "Hello world!";
    }
}