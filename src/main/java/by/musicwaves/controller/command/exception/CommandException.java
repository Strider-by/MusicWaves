package by.musicwaves.controller.command.exception;

public class CommandException extends Exception {

    public CommandException() {

    }

    public CommandException(String msg) {
        super(msg);
    }

    public CommandException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CommandException(Throwable cause) {
        super(cause);
    }
}
