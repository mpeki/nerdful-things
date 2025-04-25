package dk.pekilidi.exceptions;

import java.io.Serial;

public class CannotReadPropertiesException extends RuntimeException {
    
    @Serial
    private static final long serialVersionUID = -3725478554865248954L;

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
