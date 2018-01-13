package vsp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    @Id
    @GeneratedValue
    private Long id;

    private String ip;

    private String location;

    private String name;

    private String[] delivered;

    private String[] deliverables_done;

    @OneToOne
    private Link _links;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getDeliverables_done() {
        return deliverables_done;
    }

    public void setDeliverables_done(String[] deliverables_done) {
        this.deliverables_done = deliverables_done;
    }

    public String[] getDelivered() {
        return delivered;
    }

    public void setDelivered(String[] delivered) {
        this.delivered = delivered;
    }
}
