package dk.pekilidi.properties;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class PropertyUtilTest {

    @Test
    void testLoadFromFile() throws IOException {
        Properties properties = new Properties();
        properties.setProperty("key1", "value1");
        properties.setProperty("key2", "value2");

        File tempFile = Files.createTempFile("testProperties", ".properties").toFile();
        tempFile.deleteOnExit();

        File testFile = PropertyUtil.writeToFile(properties, tempFile, "Test Comment");
        assertTrue(testFile.exists());

        Properties loadedProperties = PropertyUtil.loadFromFile(tempFile);

        assertTrue(loadedProperties.containsKey("key1"));
        assertTrue(loadedProperties.containsKey("key2"));
        assertTrue(loadedProperties.containsValue("value1"));
        assertTrue(loadedProperties.containsValue("value2"));
    }

    @Test
    void testWriteToFile() throws IOException {
        Properties properties = new Properties();
        properties.setProperty("key1", "value1");
        properties.setProperty("key2", "value2");

        File tempFile = Files.createTempFile("testProperties", ".properties").toFile();
        tempFile.deleteOnExit();

        PropertyUtil.writeToFile(properties, tempFile, "Test Comment");

        Properties loadedProperties = new Properties();
        loadedProperties.load(Files.newInputStream(tempFile.toPath()));

        assertTrue(loadedProperties.containsKey("key1"));
        assertTrue(loadedProperties.containsKey("key2"));
        assertTrue(loadedProperties.containsValue("value1"));
        assertTrue(loadedProperties.containsValue("value2"));
    }
}