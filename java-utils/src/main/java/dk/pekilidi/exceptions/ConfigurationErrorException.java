package dk.pekilidi.exceptions;

import java.io.Serial;

public class ConfigurationErrorException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -8645167391155189291L;

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
