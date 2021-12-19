package by.musicwaves.controller.command.action;

import by.musicwaves.controller.exception.CommandException;
import by.musicwaves.controller.exception.ValidationException;
import by.musicwaves.controller.util.Validator;
import by.musicwaves.controller.util.AccessLevelEnum;
import by.musicwaves.controller.util.ApplicationPageEnum;
import by.musicwaves.controller.util.TransitTypeEnum;
import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.entity.ancillary.Language;
import by.musicwaves.service.UserService;
import by.musicwaves.service.exception.ServiceException;
import by.musicwaves.service.factory.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RegisterUserCommand extends AbstractActionCommand {

    private final static String PARAM_NAME_LOGIN = "login";
    private final static String PARAM_NAME_PASSWORD_1 = "password1";
    private final static String PARAM_NAME_PASSWORD_2 = "password2";
    private final static String PARAM_NAME_INVITE_CODE = "invite_code";
    private final UserService service = ServiceFactory.getInstance().getUserService();

    public RegisterUserCommand(AccessLevelEnum accessLevelEnum) {
        super(accessLevelEnum);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, ValidationException {

        TransitTypeEnum transitTypeEnum = TransitTypeEnum.REDIRECT;
        ApplicationPageEnum targetPage = ApplicationPageEnum.ENTRANCE;

        String login = Validator.assertNonNull(request.getParameter(PARAM_NAME_LOGIN));
        char[] password1 = Validator.assertNonNull(request.getParameter(PARAM_NAME_PASSWORD_1)).toCharArray();
        char[] password2 = Validator.assertNonNull(request.getParameter(PARAM_NAME_PASSWORD_2)).toCharArray();
        String inviteCode = Validator.assertNonNull(request.getParameter(PARAM_NAME_INVITE_CODE));

        Language language = Language.DEFAULT; // todo: get from request?

        try {
            // register user if it is possible
            ServiceResponse<Integer> serviceResponse = service.registerUser(login, password1, password2, inviteCode, language);

            // attach service response object to session to be processed in jsp if required
            attachServiceResponse(request, serviceResponse);
            // going to proper page
            transfer(request, response, targetPage, transitTypeEnum);

        } catch (ServletException | IOException | ServiceException ex) {
            throw new CommandException(ex);
        }
    }

}
