package vsp.achievement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Achievment {

    @JsonCreator
    public Achievment(
            @JsonProperty("message") String message,
            @JsonProperty("token") String token,
            @JsonProperty("token_name") String token_name) {
        this.message = message;
        this.token = token;
        this.token_name = token_name;
    }

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
