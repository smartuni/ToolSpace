package vsp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Achievment {

    @Id
    @GeneratedValue
    private Long id;

    private String message;

    private String token;

    private String token_name;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken_name(String token_name) {
        this.token_name = token_name;
    }

    public String getToken_name() {
        return token_name;
    }
}
