package by.musicwaves.controller.command;

import by.musicwaves.controller.command.exception.CommandException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Represents any type of commands
 */
public interface Command {
    /**
     * Checks if user has rights to execute requested command and either execute it (if user has required rights)
     * or not (if he hasn't). In last case some additional action can be taken to show user that request has been denied.
     */
    void checkAccessAndExecute(HttpServletRequest request, HttpServletResponse response) throws CommandException;
}
