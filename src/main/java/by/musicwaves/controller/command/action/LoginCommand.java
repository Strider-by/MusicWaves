package by.musicwaves.controller.command.action;

import by.musicwaves.controller.command.exception.CommandException;
import by.musicwaves.controller.resource.ApplicationPage;
import by.musicwaves.controller.resource.TransitType;
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

public class LoginCommand extends AbstractActionCommand {

    private final static Logger LOGGER = LogManager.getLogger(LoginCommand.class);

    private final static String PARAM_NAME_LOGIN = "login";
    private final static String PARAM_NAME_PASSWORD = "password";
    private final static String SESSION_ATTRIBUTE_NAME_USER = "user";
    private final static String SESSION_ATTRIBUTE_NAME_LOCALE = "locale";

    private final UserService service = ServiceFactory.getInstance().getUserService();


    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        ApplicationPage targetPage;
        TransitType transitType = TransitType.REDIRECT;

        String login = request.getParameter(PARAM_NAME_LOGIN);
        String password = request.getParameter(PARAM_NAME_PASSWORD);
        Locale locale = Language.DEFAULT.getLocale();

        try {
            // check if we have this user in our database
            ServiceResponse<User> serviceResponse = service.login(login, password, locale);

            // if login credentials are all right
            if (serviceResponse.isSuccess()) {
                User user = serviceResponse.getStoredValue();
                HttpSession session = request.getSession();
                session.setAttribute(SESSION_ATTRIBUTE_NAME_USER, user);
                session.setAttribute(SESSION_ATTRIBUTE_NAME_LOCALE, user.getLanguage().getLocale());
                targetPage = ApplicationPage.PROFILE;
            }
            // if there is no user with such credentials
            else {
                targetPage = ApplicationPage.ENTRANCE;
            }

            // set  messages and error codes to be shown for user or to be processed by front-end
            attachServiceResponse(request, serviceResponse);

            LOGGER.debug("Service response is: \n" + serviceResponse);

            // going to proper page
            transfer(request, response, targetPage, transitType);

        } catch (ServletException | IOException | ServiceException ex) {
            throw new CommandException("Failed to execute login command", ex);
        }
    }

}
