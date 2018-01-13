package datacenter;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Ditmar on 09.12.17.
 */

@Entity
public class Token {

    @Id
    @GeneratedValue
    private Integer id;

    private String message;

    private String token;

    private Double valid_till;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Double getValid_till() {
        return valid_till;
    }

    public void setValid_till(Double valid_till) {
        this.valid_till = valid_till;
    }


}
