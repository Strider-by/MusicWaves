package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.exception.CommandException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface XHRCommand {
    void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, IOException;
}
