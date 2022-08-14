package bibernate.exception;

public class MoreThanOneResultException extends RuntimeException {
    public MoreThanOneResultException(String message) {
        super(message);
    }
}
