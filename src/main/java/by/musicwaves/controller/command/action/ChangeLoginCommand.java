package by.musicwaves.controller.command.action;

import by.musicwaves.controller.command.CommandException;
import by.musicwaves.controller.command.Validator;
import by.musicwaves.controller.resources.ApplicationPage;
import by.musicwaves.controller.resources.TransitType;
import by.musicwaves.entity.User;
import by.musicwaves.service.ServiceException;
import by.musicwaves.service.ServiceResponse;
import by.musicwaves.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ChangeLoginCommand extends ActionCommand
{
    private final static String PARAM_NAME_PASSWORD = "password";
    private final static String PARAM_NAME_LOGIN = "login";

    private final static String SESSION_ATTRIBUTE_NAME_USER = "user";

    private final static List<String> allowedRequestMethods;

    private final static UserService service = UserService.getInstance();
    private static final Logger LOGGER = LogManager.getLogger(ChangeLoginCommand.class);

    static {
        allowedRequestMethods = new ArrayList<>();
        allowedRequestMethods.add("POST");
    }


    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        this.transitType = TransitType.REDIRECT;

        char[] password = Validator.assertNonNull(request.getParameter(PARAM_NAME_PASSWORD)).toCharArray();
        String newLogin = Validator.assertNonNull(request.getParameter(PARAM_NAME_LOGIN));
        User user = (User) request.getSession().getAttribute(SESSION_ATTRIBUTE_NAME_USER);
        String requestMethod = request.getMethod();

        try {
            // non-allowed request method usage will cause CommandException throw
            Validator.assertAllowedRequestMethod(requestMethod, allowedRequestMethods);

            // user is not logged in (m.b. session has expired)
            if (user == null) {
                this.targetPage = ApplicationPage.ENTRANCE;
                transfer(request, response);
                return;
            }

            // running service
            ServiceResponse<String> serviceResponse = service.changeLogin(user, password, newLogin);

            if (serviceResponse.isSuccess()) {
                // if service succeeded
                // we want user to log in anew after his login has been changed
                request.getSession().invalidate();
                this.targetPage = ApplicationPage.ENTRANCE;
            } else {
                this.targetPage = ApplicationPage.PROFILE;
                // set  messages and error codes to be shown for user or to be processed by front-end
                attachServiceResponse(request, serviceResponse);
            }


            LOGGER.debug("Service response is: \n" + serviceResponse);

            // going to target page
            transfer(request, response);

        } catch(ServletException | IOException | ServiceException ex) {
            throw new CommandException("Failed to execute command", ex);
        }
    }



}
