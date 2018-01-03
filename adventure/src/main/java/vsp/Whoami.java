package vsp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import vsp.user.User;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Whoami {

    @Id
    @GeneratedValue
    private Long id;

    private String message;

    @OneToOne
    private User user;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
