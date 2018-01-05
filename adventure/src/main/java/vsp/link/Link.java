package vsp.link;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Link {

    @JsonCreator
    public Link(@JsonProperty("encryption_key") String encryption_key,
                @JsonProperty("self") String self,
                @JsonProperty("deliveries") String deliveries,
                @JsonProperty("tasks") String tasks) {
        this.encryption_key = encryption_key;
        this.self = self;
        this.deliveries = deliveries;
        this.tasks = tasks;
    }

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

    @Override
    public String toString() {
        return "Link{" +
                "encryption_key='" + encryption_key + '\'' +
                ", self='" + self + '\'' +
                ", deliveries='" + deliveries + '\'' +
                ", tasks='" + tasks + '\'' +
                '}';
    }
}
