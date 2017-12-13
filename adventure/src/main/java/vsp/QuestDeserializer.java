package vsp;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.util.List;

/**
 * Created by Ditmar on 12.12.17.
 */
public class QuestDeserializer extends StdDeserializer<Quest> {

    public QuestDeserializer() {
        this(null);
    }

    public QuestDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
public Quest deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = jp.getCodec().readTree(jp);
        Quest quest = new Quest();
        quest.setId(node.get("object").get("id").intValue());
        quest.set_links(mapper.readValue(node.get("object").get("_links").textValue(), Link.class));
        quest.setDescription(node.get("object").get("description").textValue());
        quest.setFollowups(node.get("object").get("followups").textValue()/*wrapper.getValues()*/);
        quest.setName(node.get("object").get("name").textValue());
        quest.setFollowups(node.get("object").get("followups").textValue()/*wrapper.getValues()*/);
        quest.setRequirements(node.get("object").get("requirements").textValue());
        quest.setReward(node.get("object").get("reward").intValue());
        quest.setFollowups(node.get("object").get("followups").textValue()/*wrapper.getValues()*/);

        return quest;
    }

}
