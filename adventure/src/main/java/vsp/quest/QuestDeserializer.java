package vsp.quest;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import vsp.facade.ApplicationFacadeController;
import vsp.link.Link;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by Ditmar on 12.12.17.
 */
public class QuestDeserializer extends JsonDeserializer<Quest> {

    static Logger log =  Logger.getLogger(ApplicationFacadeController.class);

    @Override
    public Quest deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        ObjectMapper map = new ObjectMapper();
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);
        final Integer id = node.get("object").get("id").intValue();
        final String description = node.get("object").get("description").textValue();
        final String followups = node.get("object").get("followups").textValue();
        final String name = node.get("object").get("name").textValue();
        final String prerequisits = node.get("object").get("followups").textValue();
        final String requirements = node.get("object").get("requirements").textValue();
        final Integer reward = node.get("object").get("reward").intValue();
        ArrayList<String> tasks = new ArrayList<>();
        final JsonNode arrNode = node.get("object").get("tasks");
        for (final JsonNode objNode : arrNode){
            log.info(objNode.asText() + " : " + objNode.textValue());
                tasks.add(objNode.textValue());
        }


        final String self = node.get("object").get("_links").get("self").textValue();
        final String deliveries = node.get("object").get("_links").get("deliveries").textValue();
        final String tasksL = node.get("object").get("_links").get("tasks").textValue();

        Link link = new Link(null, self, deliveries, tasksL);

        return new Quest(id, link, description, followups, name, prerequisits, requirements, reward, tasks);
    }

}
