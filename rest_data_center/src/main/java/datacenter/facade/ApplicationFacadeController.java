package datacenter.facade;

import datacenter.*;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Ditmar on 09.12.17.
 */

@RestController
public class ApplicationFacadeController {

    @RequestMapping(value ="/token", method = RequestMethod.GET)
    public Token getToken(){
        Token token = new Token();
        token.setMessage("Hallo");
        token.setToken("sua73298ibc467329874nbwqgnxgi7xi");
        token.setValid_till(754.213);
        return token;
    }
}
