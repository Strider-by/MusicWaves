package by.musicwaves.controller.command.action;

import by.musicwaves.controller.exception.CommandException;
import by.musicwaves.controller.exception.ValidationException;
import by.musicwaves.controller.util.Validator;
import by.musicwaves.controller.util.AccessLevelEnum;
import by.musicwaves.controller.util.ApplicationPageEnum;
import by.musicwaves.controller.util.TransitTypeEnum;
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

public class ChangePasswordCommand extends AbstractActionCommand {

    private final static Logger LOGGER = LogManager.getLogger(ChangePasswordCommand.class);
    private final static String PARAM_NAME_OLD_PASSWORD = "old_password";
    private final static String PARAM_NAME_NEW_PASSWORD_1 = "new_password_1";
    private final static String PARAM_NAME_NEW_PASSWORD_2 = "new_password_2";
    private final List<String> allowedRequestMethods;
    private final UserService service = ServiceFactory.getInstance().getUserService();

    {
        allowedRequestMethods = new ArrayList<>();
        allowedRequestMethods.add("POST");
    }

    public ChangePasswordCommand(AccessLevelEnum accessLevelEnum) {
        super(accessLevelEnum);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, ValidationException {
        ApplicationPageEnum targetPage;
        TransitTypeEnum transitTypeEnum = TransitTypeEnum.REDIRECT;

        char[] oldPassword = Validator.assertNonNull(request.getParameter(PARAM_NAME_OLD_PASSWORD)).toCharArray();
        char[] newPassword1 = Validator.assertNonNull(request.getParameter(PARAM_NAME_NEW_PASSWORD_1)).toCharArray();
        char[] newPassword2 = Validator.assertNonNull(request.getParameter(PARAM_NAME_NEW_PASSWORD_2)).toCharArray();
        User user = getUser(request);
        String requestMethod = request.getMethod();

        try {
            // non-allowed request method usage will cause ValidationException throw
            Validator.assertIsAllowedRequestMethod(requestMethod, allowedRequestMethods);

            // running service
            ServiceResponse<String> serviceResponse = service.changePassword(user, oldPassword, newPassword1, newPassword2);
            if (serviceResponse.isSuccess()) {
                // if service succeeded we want user to log in anew after his password has been changed
                request.getSession().invalidate();
                targetPage = ApplicationPageEnum.ENTRANCE;
            } else {
                targetPage = ApplicationPageEnum.PROFILE;
            }

            // set  messages and error codes to be shown for user or to be processed by front-end
            attachServiceResponse(request, serviceResponse);
            // going to target page
            transfer(request, response, targetPage, transitTypeEnum);

        } catch (ServletException | IOException | ServiceException ex) {
            throw new CommandException("Failed to execute command", ex);
        }
    }
}
