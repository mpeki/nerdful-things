package dk.pekilidi.properties;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Properties;

@Slf4j
public class PropertyUtil {

    private PropertyUtil() {
    }

    public static Properties loadFromFile(File file) throws IOException {
        Properties properties = new Properties();
        try {
            properties.load(Files.newInputStream(file.toPath()));
        } catch (IOException e) {
            log.error("Failed to load properties from file", e);
            throw e;
        }
        return properties;
    }

    public static File writeToFile(Properties properties, File file, String comment) throws IOException {
        try (OutputStream out = Files.newOutputStream(file.toPath())) {
            properties.store(out, comment);
        } catch (IOException e) {
            log.error("Failed to write properties to file", e);
            throw e;
        }
        return file;
    }
}
