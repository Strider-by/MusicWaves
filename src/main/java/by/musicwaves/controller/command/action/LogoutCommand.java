package by.musicwaves.controller.command.action;

import by.musicwaves.controller.command.exception.CommandException;
import by.musicwaves.controller.resource.ApplicationPage;
import by.musicwaves.controller.resource.TransitType;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogoutCommand extends AbstractActionCommand {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        TransitType transitType = TransitType.REDIRECT;
        ApplicationPage targetPage = ApplicationPage.ENTRANCE;

        // there is no need to call any service method here
        // all the current login data is stored in session
        request.getSession().invalidate();

        try {
            // going to entrance page
            transfer(request, response, targetPage, transitType);
        } catch (ServletException | IOException ex) {
            throw new CommandException("Failed to execute logout command", ex);
        }
    }
}
