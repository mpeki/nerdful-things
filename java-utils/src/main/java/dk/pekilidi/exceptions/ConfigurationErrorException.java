package dk.pekilidi.exceptions;

public class ConfigurationErrorException extends RuntimeException {
    public ConfigurationErrorException(String message) {
        super(message);
    }

    public ConfigurationErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationErrorException(Throwable cause) {
        super(cause);
    }
}
