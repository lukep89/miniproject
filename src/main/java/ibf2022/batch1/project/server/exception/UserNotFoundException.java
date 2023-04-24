package ibf2022.batch1.project.server.exception;

public class UserNotFoundException extends Exception {

    public UserNotFoundException(String message, Throwable ex) {
        super(message, ex);
    }
}
