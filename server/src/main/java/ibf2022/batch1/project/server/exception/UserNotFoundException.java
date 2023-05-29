package ibf2022.batch1.project.server.exception;

public class UserNotFoundException extends Exception {

    // public UserNotFoundException() {
    //     CafeUtils.getRespEntity(HttpStatus.BAD_REQUEST, "User not found");
    // }

    public UserNotFoundException(String message, Throwable ex) {
    super(message, ex);
    }
}
