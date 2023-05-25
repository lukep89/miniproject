package ibf2022.batch1.project.server.exception;

import org.springframework.http.HttpStatus;

import ibf2022.batch1.project.server.utils.CafeUtils;

public class UserNotFoundException extends Exception {

    public UserNotFoundException() {
        CafeUtils.getRespEntity(HttpStatus.BAD_REQUEST, "User not found");
    }

    // public UserNotFoundException(String message, Throwable ex) {
    // super(message, ex);
    // }
}
