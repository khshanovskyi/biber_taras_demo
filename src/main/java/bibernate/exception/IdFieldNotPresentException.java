package bibernate.exception;

public class IdFieldNotPresentException extends RuntimeException {
    public IdFieldNotPresentException(String message) {
        super(message);
    }
}
