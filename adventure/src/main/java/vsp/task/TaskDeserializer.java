package vsp.task;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.log4j.Logger;
import vsp.facade.ApplicationFacadeController;
import vsp.link.Link;

import java.io.IOException;

public class TaskDeserializer extends JsonDeserializer<Task> {

    static Logger log =  Logger.getLogger(ApplicationFacadeController.class);

    @Override
    public Task deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);

        final Integer id = node.get("object").get("id").intValue();
        final String description = node.get("object").get("description").textValue();
        final String location = node.get("object").get("location").textValue();
        final String name = node.get("object").get("name").textValue();
        final Integer quest = node.get("object").get("quest").intValue();
        final Integer required_players = node.get("object").get("required_players").intValue();
        final String requirements = node.get("object").get("requirements").textValue();
        final String resource = node.get("object").get("resource").textValue();
        final String token = node.get("object").get("token").textValue();

        final String self = node.get("object").get("_links").get("self").textValue();
        Link link = new Link(null, self, null, null);

        return new Task(id, link, description, location, name, quest, required_players, requirements, resource, token);
    }

}
