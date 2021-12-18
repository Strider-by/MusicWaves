package by.musicwaves.controller.command.action;

import by.musicwaves.controller.exception.CommandException;
import by.musicwaves.controller.exception.ValidationException;
import by.musicwaves.controller.util.Validator;
import by.musicwaves.controller.util.AccessLevelEnum;
import by.musicwaves.controller.util.ApplicationPageEnum;
import by.musicwaves.controller.util.TransitTypeEnum;
import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.entity.User;
import by.musicwaves.entity.ancillary.Language;
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
import java.util.Locale;

import static by.musicwaves.controller.util.Constant.SESSION_ATTRIBUTE_NAME_USER;

public class LoginCommand extends AbstractActionCommand {

    private final static Logger LOGGER = LogManager.getLogger(LoginCommand.class);
    private final static String PARAM_NAME_LOGIN = "login";
    private final static String PARAM_NAME_PASSWORD = "password";
    private final static String SESSION_ATTRIBUTE_NAME_LOCALE = "locale";
    private final UserService service = ServiceFactory.getInstance().getUserService();


    public LoginCommand(AccessLevelEnum accessLevelEnum) {
        super(accessLevelEnum);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, ValidationException {

        ApplicationPageEnum targetPage;
        TransitTypeEnum transitTypeEnum = TransitTypeEnum.REDIRECT;

        String login = Validator.assertNonNull(request.getParameter(PARAM_NAME_LOGIN));
        char[] password = Validator.assertNonNull(request.getParameter(PARAM_NAME_PASSWORD)).toCharArray();

        Locale locale = request.getLocale();
        // todo: do I need this check? Can null value be returned when I invoke this?
        if (locale == null) {
            locale = Language.DEFAULT.getLocale();
        }

        try {
            // check if we have this user in our database
            ServiceResponse<User> serviceResponse = service.login(login, password, locale);
            // if login credentials are all right
            if (serviceResponse.isSuccess()) {
                User user = serviceResponse.getStoredValue();
                // invalidate old session to be sure there is no old data left stored
                request.getSession().invalidate();
                HttpSession session = request.getSession();
                session.setAttribute(SESSION_ATTRIBUTE_NAME_USER, user);
                session.setAttribute(SESSION_ATTRIBUTE_NAME_LOCALE, user.getLanguage().getLocale());
                targetPage = ApplicationPageEnum.PROFILE;
            }
            // if there is no user with such credentials
            else {
                targetPage = ApplicationPageEnum.ENTRANCE;
            }

            // set  messages and error codes to be shown for user or to be processed by front-end
            attachServiceResponse(request, serviceResponse);
            // going to proper page
            transfer(request, response, targetPage, transitTypeEnum);

        } catch (ServletException | IOException | ServiceException ex) {
            throw new CommandException("Failed to execute login command", ex);
        }
    }

}
