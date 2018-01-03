package vsp.room;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.ArrayList;

@JsonRootName(value ="object")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = RoomDeserializer.class)
@Entity
public class Room {

    @JsonCreator
    public Room(
            @JsonProperty("host") String host,
            @JsonProperty("name") String name,
            @JsonProperty("tasks") ArrayList<Integer> tasks,
            @JsonProperty("visitors") ArrayList<String> visitors) {
        this.host = host;
        this.name = name;
        this.tasks = tasks;
        this.visitors = visitors;
    }

    @Id
    @GeneratedValue
    private Long id;

    private String host;

    private String name;

    private ArrayList<Integer> tasks;

    private ArrayList<String> visitors;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Integer> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Integer> tasks) {
        this.tasks = tasks;
    }

    public ArrayList<String> getVisitors() {
        return visitors;
    }

    public void setVisitors(ArrayList<String> visitors) {
        this.visitors = visitors;
    }

    @Override
    public String toString() {
        return "Room{" +
                "host='" + host + '\'' +
                ", name='" + name + '\'' +
                ", tasks='" + tasks + '\'' +
                ", visitors='" + visitors + '\'' +
                '}';
    }
}
