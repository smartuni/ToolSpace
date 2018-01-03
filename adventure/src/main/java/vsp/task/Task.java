package vsp.task;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import vsp.link.Link;
import vsp.quest.QuestDeserializer;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@JsonRootName(value ="object")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = TaskDeserializer.class)
@Entity
public class Task {

    @JsonCreator
    public Task(
            @JsonProperty("id") Integer id,
            @JsonProperty("_links") Link _links,
            @JsonProperty("description") String description,
            @JsonProperty("location") String location,
            @JsonProperty("name") String name,
            @JsonProperty("quest") Integer quest,
            @JsonProperty("required_players") Integer required_players,
            @JsonProperty("requirements") String requirements,
            @JsonProperty("resource") String resource,
            @JsonProperty("token") String token) {
        this.id = id;
        this._links = _links;
        this.description = description;
        this.location = location;
        this.name = name;
        this.quest = quest;
        this.required_players = required_players;
        this.requirements = requirements;
        this.resource = resource;
        this.token = token;
    }

    @Id
    private Integer id;

    @OneToOne
    private Link _links;

    private String description;

    private String location;

    private String name;

    private Integer quest;

    private Integer required_players;

    private String requirements;

    private String resource;

    private String token;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Link get_links() {
        return _links;
    }

    public void set_links(Link _links) {
        this._links = _links;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getQuest() {
        return quest;
    }

    public void setQuest(Integer quest) {
        this.quest = quest;
    }

    public Integer getRequired_players() {
        return required_players;
    }

    public void setRequired_players(Integer required_players) {
        this.required_players = required_players;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", _links=" + _links +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", name='" + name + '\'' +
                ", quest=" + quest +
                ", required_players=" + required_players +
                ", requirements='" + requirements + '\'' +
                ", resource='" + resource + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
