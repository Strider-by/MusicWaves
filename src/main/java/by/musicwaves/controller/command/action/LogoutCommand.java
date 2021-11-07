package by.musicwaves.controller.command.action;

import by.musicwaves.controller.command.CommandException;
import by.musicwaves.controller.resources.ApplicationPage;
import by.musicwaves.controller.resources.TransitType;
import by.musicwaves.service.ServiceException;
import by.musicwaves.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogoutCommand extends ActionCommand {

    private final static UserService service = UserService.getInstance();
    private static final Logger LOGGER = LogManager.getLogger(LogoutCommand.class);

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        this.transitType = TransitType.REDIRECT;
        this.targetPage = ApplicationPage.ENTRANCE;

        // there is no need to call any service method here
        // all the current login data is stored in session
        request.getSession().invalidate();

        try {
            // going to entrance page
            transfer(request, response);
        } catch (ServletException | IOException ex) {
            throw new CommandException("Failed to execute logout command", ex);
        }
    }
}
