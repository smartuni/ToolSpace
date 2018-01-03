package vsp.login;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Login {

    @Id
    @GeneratedValue
    private Long id;

    private String message;

    private String token;

    private double valid_till;

    public Login() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public double getValid_till() {
        return valid_till;
    }

    public void setValid_till(double valid_till) {
        this.valid_till = valid_till;
    }

    @Override
    public String toString() {
        return "Login{" +
                "token='" + token +
                '}';
    }
}
