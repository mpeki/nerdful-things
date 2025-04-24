package dk.pekilidi.exceptions;

public class CannotReadPropertiesException extends RuntimeException {
    public CannotReadPropertiesException(String message) {
        super(message);
    }

    public CannotReadPropertiesException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotReadPropertiesException(Throwable cause) {
        super(cause);
    }
}
