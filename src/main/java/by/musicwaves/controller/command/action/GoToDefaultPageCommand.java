package by.musicwaves.controller.command.action;

import by.musicwaves.controller.exception.CommandException;
import by.musicwaves.controller.resource.AccessLevel;
import by.musicwaves.controller.resource.ApplicationPage;
import by.musicwaves.controller.resource.TransitType;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GoToDefaultPageCommand extends AbstractActionCommand {

    public GoToDefaultPageCommand(AccessLevel accessLevel) {
        super(accessLevel);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {

        ApplicationPage targetPage = ApplicationPage.ENTRANCE;
        TransitType transitType = TransitType.REDIRECT;

        try {
            transfer(request, response, targetPage, transitType);
        } catch (ServletException | IOException ex) {
            throw new CommandException("Failed to execute Go to default page command", ex);
        }
    }
}
