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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ChangeLoginCommand extends AbstractActionCommand {

    private final static Logger LOGGER = LogManager.getLogger(ChangeLoginCommand.class);
    private final static String PARAM_NAME_PASSWORD = "password";
    private final static String PARAM_NAME_LOGIN = "login";
    private final List<String> allowedRequestMethods;
    private final UserService service = ServiceFactory.getInstance().getUserService();

    {
        allowedRequestMethods = new ArrayList<>();
        allowedRequestMethods.add("POST");
    }

    public ChangeLoginCommand(AccessLevel accessLevel) {
        super(accessLevel);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, ValidationException {
        ApplicationPage targetPage;
        TransitType transitType = TransitType.REDIRECT;

        char[] password = Validator.assertNonNull(request.getParameter(PARAM_NAME_PASSWORD)).toCharArray();
        String newLogin = Validator.assertNonNull(request.getParameter(PARAM_NAME_LOGIN));
        User user = getUser(request);
        String requestMethod = request.getMethod();

        try {
            // non-allowed request method usage will cause ValidationException throw
            Validator.assertIsAllowedRequestMethod(requestMethod, allowedRequestMethods);

            // running service
            ServiceResponse<String> serviceResponse = service.changeLogin(user, password, newLogin);
            if (serviceResponse.isSuccess()) {
                // if service succeeded we want user to log in anew after his login has been changed
                request.getSession().invalidate();
                targetPage = ApplicationPage.ENTRANCE;
            } else {
                targetPage = ApplicationPage.PROFILE;
                // set  messages and error codes to be shown for user or to be processed by front-end
                attachServiceResponse(request, serviceResponse);
            }

            // going to target page
            transfer(request, response, targetPage, transitType);

        } catch (ServletException | IOException | ServiceException ex) {
            throw new CommandException("Failed to execute command", ex);
        }
    }
}
