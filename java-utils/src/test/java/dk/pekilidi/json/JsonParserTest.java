package dk.pekilidi.json;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JsonParserTest {

    @Test
    public void testParse() {
        String json = "{\"name\":\"John\", \"age\":30}";
        String key = "name";
        String expectedValue = "John";

        String actualValue = JsonParser.parse(json, key);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testParseNonExistentKey() {
        String json = "{\"name\":\"John\", \"age\":30}";
        String key = "address";

        assertThrows(NullPointerException.class, () -> {
            JsonParser.parse(json, key);
        });
    }

    @Test
    public void testWithCancellationRequest(){
        String json = """
                {"requestId":"VIR172833","responseId":"VIR174012","version":"1.0","companyMessageId":"cm_nelrjpwnxtfjhpqw4h79k3xw","requestDate":"2025-03-10T14:09:08Z","referenceNumber":"2587565-16599249","industryProductGroup":"001/001","registrationNumber":"BD20411","vinNumber":"VSSZZZ5FZGR095676","cancellationCause":285,"cancellationDate":"2025-06-01","customers":[{"idQualifier":"CPR","id":"0112940448","name":"Kamilla","lastName":"Jakobsen","streetAddress":"Nørretorv","streetNumber":"15","floor":"02","door":"TH","postalCode":"4100","postalDistrict":"Ringsted","consentForm":true}]}
                """;
        String key = "version";
        String expectedValue = "1.0";

        System.out.println(JsonParser.prettyPrint(json));

        String actualValue = JsonParser.parse(json, key);

        assertEquals(expectedValue, actualValue);
    }
}