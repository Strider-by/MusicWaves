package by.musicwaves.controller.command.action;

import by.musicwaves.controller.command.exception.CommandException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ActionCommand {
    void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException;
}
