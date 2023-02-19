package manager.exceptions;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String message, Throwable cause) {
        super(message, cause);
    }
    public ManagerSaveException(String message) {
        super(message);
    }

}
