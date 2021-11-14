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

public class ChangePasswordCommand extends ActionCommand
{
    private final static String PARAM_NAME_OLD_PASSWORD = "old_password";
    private final static String PARAM_NAME_NEW_PASSWORD_1 = "new_password_1";
    private final static String PARAM_NAME_NEW_PASSWORD_2 = "new_password_2";

    private final static String SESSION_ATTRIBUTE_NAME_USER = "user";

    private final static List<String> allowedRequestMethods;

    private final static UserService service = UserService.getInstance();
    private static final Logger LOGGER = LogManager.getLogger(ChangePasswordCommand.class);

    static {
        allowedRequestMethods = new ArrayList<>();
        allowedRequestMethods.add("POST");
    }


    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        this.transitType = TransitType.REDIRECT;

        char[] oldPassword = Validator.assertNonNull(request.getParameter(PARAM_NAME_OLD_PASSWORD)).toCharArray();
        char[] newPassword1 = Validator.assertNonNull(request.getParameter(PARAM_NAME_NEW_PASSWORD_1)).toCharArray();
        char[] newPassword2 = Validator.assertNonNull(request.getParameter(PARAM_NAME_NEW_PASSWORD_2)).toCharArray();
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
            ServiceResponse<String> serviceResponse = service.changePassword(user, oldPassword, newPassword1, newPassword2);

            if (serviceResponse.isSuccess()) {
                // if service succeeded
                // we want user to log in anew after his password has been changed
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
