package vsp.quest;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import vsp.link.Link;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@JsonRootName(value ="object")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = QuestDeserializer.class)
@Entity
public class Quest {

    @Id
    private Integer id;

    @OneToOne
    private Link _links;

    private String description;

    private String followups;

    private String name;

    private String prerequisites;

    private String requirements;

    private Integer reward;

    private ArrayList<String> tasks;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public Integer getReward() {
        return reward;
    }

    public void setReward(Integer reward) {
        this.reward = reward;
    }

    public String getFollowups() {
        return followups;
    }

    public void setFollowups(String followups) {
        this.followups = followups;
    }

    public String getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(String prerequisites) {
        this.prerequisites = prerequisites;
    }

    public List<String> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<String> tasks) {
        this.tasks = tasks;
    }

    @JsonCreator
    public Quest(@JsonProperty("id") Integer id,
                 @JsonProperty("_links") Link _links,
                 @JsonProperty("description") String description,
                 @JsonProperty("followups") String followups,
                 @JsonProperty("name") String name,
                 @JsonProperty("prerequisites") String prerequisites,
                 @JsonProperty("requirements") String requirements,
                 @JsonProperty("reward") Integer reward,
                 @JsonProperty("tasks") ArrayList<String> tasks) {
        this.id = id;
        this._links = _links;
        this.description = description;
        this.followups = followups;
        this.name = name;
        this.prerequisites = prerequisites;
        this.requirements = requirements;
        this.reward = reward;
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "Quest{" +
                "_links=" + _links.toString() +
                ", description='" + description + '\'' +
                ", followups='" + followups + '\'' +
                ", name='" + name + '\'' +
                ", prerequisites='" + prerequisites + '\'' +
                ", requirements='" + requirements + '\'' +
                ", reward=" + reward +
                ", tasks=" + tasks.toString()+
                '}';
    }
}
