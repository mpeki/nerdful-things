package dk.pekilidi.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonUtil {

    private JsonUtil() {
        // Utility class; prevent instantiation.
    }

    /**
     * Parses the JSON, checks if the value at {@code key} is an array,
     * and returns its size. If the key isn't present or the value isn't an array,
     * returns -1.
     *
     * @param json the JSON string
     * @param key the key to check
     * @return the size of the array if present, otherwise -1
     */
    public static int getJsonArraySize(String json, String key) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(json);
            JsonNode node = root.path(key);

            // Check if the node is an array
            if (node.isArray()) {
                return node.size();
            } else {
                return -1;
            }
        } catch (Exception e) {
            // If parsing fails or anything goes wrong, return -1
            return -1;
        }
    }
}
