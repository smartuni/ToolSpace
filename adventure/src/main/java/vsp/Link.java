package vsp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Link {

    @Id
    @GeneratedValue
    private Long id;

    private String encryption_key;

    private String self;

    private String deliveries;

    private String tasks;

    public String getEncryption_key() {
        return encryption_key;
    }

    public void setEncryption_key(String encryption_key) {
        this.encryption_key = encryption_key;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(String deliveries) {
        this.deliveries = deliveries;
    }

    public String getTasks() {
        return tasks;
    }

    public void setTasks(String tasks) {
        this.tasks = tasks;
    }
}
