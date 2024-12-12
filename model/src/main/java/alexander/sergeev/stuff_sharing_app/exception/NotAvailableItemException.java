package alexander.sergeev.stuff_sharing_app.exception;

public class NotAvailableItemException extends RuntimeException {

    public NotAvailableItemException(String message) {
        super(message);
    }
}