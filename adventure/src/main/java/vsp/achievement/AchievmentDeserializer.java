package vsp.achievement;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class AchievmentDeserializer extends JsonDeserializer<Achievment> {

    @Override
    public Achievment deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);
        final String message =node.get("object").get("host").textValue();
        final String token = node.get("object").get("token").textValue();
        final String token_name = node.get("object").get("token_name").textValue();

        return new Achievment(message, token, token_name);
    }

}
