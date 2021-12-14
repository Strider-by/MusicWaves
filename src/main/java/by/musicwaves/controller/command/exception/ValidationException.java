package by.musicwaves.controller.command.exception;

public class ValidationException extends Exception {

    public ValidationException() {

    }

    public ValidationException(String msg) {
        super(msg);
    }

    public ValidationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }
}
