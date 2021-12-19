package by.musicwaves.controller.command.action;

import by.musicwaves.controller.exception.CommandException;
import by.musicwaves.controller.util.AccessLevelEnum;
import by.musicwaves.controller.util.ApplicationPageEnum;
import by.musicwaves.controller.util.TransitTypeEnum;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GoToDefaultPageCommand extends AbstractActionCommand {

    public GoToDefaultPageCommand(AccessLevelEnum accessLevelEnum) {
        super(accessLevelEnum);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {

        ApplicationPageEnum targetPage = ApplicationPageEnum.ENTRANCE;
        TransitTypeEnum transitTypeEnum = TransitTypeEnum.REDIRECT;

        try {
            transfer(request, response, targetPage, transitTypeEnum);
        } catch (ServletException | IOException ex) {
            throw new CommandException("Failed to execute Go to default page command", ex);
        }
    }
}
