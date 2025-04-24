package dk.pekilidi.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class JsonParser {

    private JsonParser() {
    }

    public static String parse(String json, String key) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = mapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        // Then extract by key:
        return node.get(key).asText();
    }

    public static String prettyPrint(String json) {
        if(json == null) {
            log.error("Json is null");
            return "";
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            Object jsonObject = mapper.readValue(json, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void savePrettyPrintToFile(String json, String filePath) {
        if(json == null || filePath == null) {
            log.error("Json or file path is null");
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            Object jsonObject = mapper.readValue(json, Object.class);
            String prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
            Files.writeString(Paths.get(filePath), prettyJson);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
