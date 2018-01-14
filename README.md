# SmartToolStation
  
WS 2017/18 - RIOT im Internet of Things - Bachelor Project of Computer Science and Electrical Engineering 
   
# What is ToolSpace?
  
ToolSpace is a management system for monitoring and administration of a tool workspace.
The goal is to develop a system, wich can be easy installed and manage diffrent tools in a comfortable and smart way.
At the entrance of the workspace, the user sign in by NFC. To rent a tool, the user first loggs into the rental terminal, afterwards he scan the required tools.
Every tool and every user has his unigue NFC tag. 
To return the tools, the user only has to place the tool back to the tool wall.
        
![Toolspaceübersicht](images/ToolSpace_Uebersicht.jpg)

**Toolwall**
  
  - A: Tool with NFC tag (:hammer:)
  - B: Tool with NFC tag (chisel)
  - C: bold scale
  - D: gauge with NFC tag, reading device
  - E: gauge with NFC tag, reading device, electronical lock
  


    
# System Overview
   
![Planungsübersicht](images/aufbau_Toolspace.JPG)


## Sensor

For detecting the tools at the wall or for logging into the Toolstation, we use NFC tags. The RIOT Bord is connected with NFC sensors. Every information about the system will be send to the backend, where it will be handled and stored. For example the login of an already registered user looks like:

``` c
put("fe80::1ac0:ffee:1ac0:ffee","/login", testdatenNeu);
```

The variable `testdatenNeu` contains the NFC tag, send to the `LoginRepository` of the `Applicatinfaceadecontroller` in the backend via gateway. When a user tries to log in, a green or red LED will signalize the success or failure.

## Gateway

The gateway has its use in translating `COAP` to `HTTP` or backwards. The commmunication to the Riot boards works via COAP and with the backend via HTTP. This translation is handled via `CrossCOAP` (GO). The commmunication to the backendserver runs via HAW router and the internet. Communication out of the HAW works fine, but wehn the backendservers tries to answer the gateway request, it ist not allowed to use any of the router ports. A first solution would be, to unlock a port, but that is to difficult and not allowed in the university. The second solution is, to use an SSH tunnel. The process for assambling the SSH tunnel will look like:

``` go
...
pi@raspberrypi:~cd .ssh		
pi@raspberrypi:~cat id_rsa.pub		//show publickey
ssh-rsa XXX pi@raspberrypi		//copy all

****************************Shell changing to Servername@user:~$
Servername@user:~$ cd .ssh
Servername@user:~/.ssh$ ls	//no authorized_keys avalible
Servername@user:~/.ssh$ nano authorized_keys // copy "ssh-rsa XXX pi@raspberrypi" into the file
Servername@user:~/.ssh$ ls	//control for authorization
authorized_keys				// Keys authorized
Servername@user:~/.ssh$ chmod 600 authorized_keys	//authorization

****************************Shell changing to pi@raspberrypi~ $
pi@raspberrypi~ $ ssh -fN -R 3000:localhost:3001 Servername@000.000.000.000 //reverse ssh
//3001: Server
//3000: Raspy an Servererver
...
```

## Backend

In the backend most of the logic processing and handling happens. Requests from the gateway works via HTTP. The whole Back- and Frontend is build with the `Spring Framework` and `Angular`. For the first try of a working webserver we used `Apache2` and `PHP`. The database is build up with `MySQL`. There are two databases, one for the user (name, userLevel, logStatus, userHash) and one for all tools (name, toolLevel, atWall, atRoom). Via REST all information is shown on our [Toolspace website](http://141.22.28.87/). The `ApplicationFacadeController`, written in TypeScript, handles every actions for the Back- and Frontend. The following Code shows the handeling of the login (`/login`) request. When the user NFC tag, that was transmitted by the gateway, is found in the user database, the function returns a "202" (acceptet). That will be transmitted via SSH tunnel(`http://localhost:3000`) to the gateway.

``` ts
    @RequestMapping(value="/login", method = RequestMethod.PUT, consumes = {MediaType.TEXT_PLAIN_VALUE}, produces = "text/plain")
    @ResponseBody
    public ResponseEntity logUser(@RequestBody String user_nfc){
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                String status_pos = "202";
                String status_neg = "410";
        try{
                User us = userRepository.findByNfc(user_nfc);
                if (us.getLogin() == 0){
                        us.setLogin(1);
                } else {
                        us.setLogin(0);
                }
                userRepository.saveAndFlush(us);

                HttpEntity<String> requestUpdate = new HttpEntity(status_pos, headers);
                ResponseEntity<String> something = restTemplate.exchange("http://localhost:3000", HttpMethod.PUT, requestUpdate, String.class);
                return new ResponseEntity(HttpStatus.ACCEPTED);
        }catch(Exception e) {
                HttpEntity<String> requestUpdate = new HttpEntity(status_neg, headers);
                restTemplate.exchange("http://localhost:3000", HttpMethod.PUT, requestUpdate, Void.class);
                return new ResponseEntity(HttpStatus.GONE);
        }
    }
```

## Frontend

The frontend is designed, to show all relevant Toolspace information to the user, like who is logged in or which tool is borrowed. Simple buttons can be clicked to show the desired information. 

![Websitdesign](images/website.JPG)

The following code displays the formatted user table on the website:

``` ts
 @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String getAllUser(){
        List<User> lu =  userRepository.findAll();
        String val = "";
        String log = "";
        for(User u : lu){
                log = u.getLogin().equals(1)?"Logged in":"Logged out";
                val = val + "| " + u.getName() + " | " + log + " |\n";

        }
        return val;
    }
```

``` html
| User    | Status     |
------------------------
| Nina    | Logged in  |
| Andreas | Logged out |
| Tim     | Logged in  |
| Simon   | Logged out |
| Peter   | Logged out |
```

Starting the WebApp in the `git/Toolspace/db` folder running the `./gradlew bootrun` command.
