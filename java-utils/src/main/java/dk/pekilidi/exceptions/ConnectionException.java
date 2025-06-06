package dk.pekilidi.exceptions;

import java.io.Serial;

public class ConnectionException extends RuntimeException {


    @Serial
    private static final long serialVersionUID = -7284957029822219968L;

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
