package by.musicwaves.controller.command.action;

import by.musicwaves.controller.exception.CommandException;
import by.musicwaves.controller.util.AccessLevelEnum;
import by.musicwaves.controller.util.ApplicationPageEnum;
import by.musicwaves.controller.util.TransitTypeEnum;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogoutCommand extends AbstractActionCommand {

    public LogoutCommand(AccessLevelEnum accessLevelEnum) {
        super(accessLevelEnum);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {

        TransitTypeEnum transitTypeEnum = TransitTypeEnum.REDIRECT;
        ApplicationPageEnum targetPage = ApplicationPageEnum.ENTRANCE;

        // there is no need to call any service method here
        // all the current login data is stored in session
        request.getSession().invalidate();

        try {
            // going to entrance page
            transfer(request, response, targetPage, transitTypeEnum);
        } catch (ServletException | IOException ex) {
            throw new CommandException("Failed to execute logout command", ex);
        }
    }
}
