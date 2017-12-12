package vsp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Task {

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
}
