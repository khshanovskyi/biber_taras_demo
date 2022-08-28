package bibernate.exception;

public class SessionIsClosedException extends RuntimeException {
    public SessionIsClosedException(String message) {
        super(message);
    }
}
