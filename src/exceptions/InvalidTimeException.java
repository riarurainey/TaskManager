package exceptions;

public class InvalidTimeException extends RuntimeException {

    public InvalidTimeException(final String message) {
        super(message);
    }
}
