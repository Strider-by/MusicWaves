package by.musicwaves.controller.command;

import by.musicwaves.controller.command.exception.CommandException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Command {
    void checkAccessAndExecute(HttpServletRequest request, HttpServletResponse response) throws CommandException;
}
