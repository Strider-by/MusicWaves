package by.musicwaves.controller.command.action;

import by.musicwaves.controller.command.exception.CommandException;
import by.musicwaves.controller.resource.ApplicationPage;
import by.musicwaves.controller.resource.TransitType;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GoToDefaultPageCommand extends AbstractActionCommand {
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {

        ApplicationPage targetPage = ApplicationPage.ENTRANCE;
        TransitType transitType = TransitType.REDIRECT;

        try {
            transfer(request, response, targetPage, transitType);
        } catch (ServletException | IOException ex) {
            throw new CommandException("Failed to execute command", ex);
        }
    }
}
