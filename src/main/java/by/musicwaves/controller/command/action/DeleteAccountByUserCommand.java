package by.musicwaves.controller.command.action;

import by.musicwaves.controller.command.exception.CommandException;
import by.musicwaves.controller.command.exception.ValidationException;
import by.musicwaves.controller.command.util.Validator;
import by.musicwaves.controller.resource.AccessLevel;
import by.musicwaves.controller.resource.ApplicationPage;
import by.musicwaves.controller.resource.TransitType;
import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.entity.User;
import by.musicwaves.service.UserService;
import by.musicwaves.service.exception.ServiceException;
import by.musicwaves.service.factory.ServiceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


public class DeleteAccountByUserCommand extends AbstractActionCommand {

    private final static Logger LOGGER = LogManager.getLogger(DeleteAccountByUserCommand.class);
    private final static String PARAM_NAME_PASSWORD = "password";
    private final UserService service = ServiceFactory.getInstance().getUserService();

    public DeleteAccountByUserCommand(AccessLevel accessLevel) {
        super(accessLevel);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, ValidationException {
        ApplicationPage targetPage;
        TransitType transitType = TransitType.REDIRECT;

        char[] password = Validator.assertNonNull(request.getParameter(PARAM_NAME_PASSWORD)).toCharArray();
        HttpSession session = request.getSession();
        User user = getUser(request);

        try {
            ServiceResponse<?> serviceResponse = service.deleteUserAccount(user, password);

            // if everything went well, we can now forget about user via invalidating session
            if (serviceResponse.isSuccess()) {
                session.invalidate();
                targetPage = ApplicationPage.ENTRANCE;
            }
            // if something went wrong (most likely - the password was incorrect)
            else {
                targetPage = ApplicationPage.PROFILE;
            }

            // set messages and error codes to be shown for user or to be processed by front-end
            attachServiceResponse(request, serviceResponse);
            // go to proper page
            transfer(request, response, targetPage, transitType);

        } catch (ServletException | IOException | ServiceException ex) {
            throw new CommandException("Failed to execute command", ex);
        }
    }
}
