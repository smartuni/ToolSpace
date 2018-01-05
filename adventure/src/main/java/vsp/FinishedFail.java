package vsp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class FinishedFail {


    @JsonCreator
    public FinishedFail(
            @JsonProperty("error") String error,
            @JsonProperty("message") String message,
            @JsonProperty("object") String object)
            {
        this.error = error;
        this.message = message;
        this.object = object;
            }

    @Id
    @GeneratedValue
    private Long id;

    private String error;

    private String message;

    private String object;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "FinishedFail{" +
                "error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", object='" + object + '\'' +
                '}';
    }
}
