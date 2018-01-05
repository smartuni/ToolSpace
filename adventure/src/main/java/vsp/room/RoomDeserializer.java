package vsp.room;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.log4j.Logger;
import vsp.facade.ApplicationFacadeController;

import java.io.IOException;
import java.util.ArrayList;

public class RoomDeserializer extends JsonDeserializer<Room> {

    static Logger log =  Logger.getLogger(ApplicationFacadeController.class);

    @Override
    public Room deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);
        final String host = node.get("object").get("host").textValue();
        final String name = node.get("object").get("name").textValue();
        ArrayList<Integer> tasks = new ArrayList<>();
        final JsonNode arrIntegerNode = node.get("object").get("tasks");
        for (final JsonNode objNode : arrIntegerNode){
            tasks.add(objNode.intValue());
        }
        ArrayList<String> visitors = new ArrayList<>();
        final JsonNode arrStringNode = node.get("object").get("visitors");
        for (final JsonNode objNode : arrStringNode){
            visitors.add(objNode.textValue());
        }

        return new Room(host, name, tasks, visitors);
    }

}
