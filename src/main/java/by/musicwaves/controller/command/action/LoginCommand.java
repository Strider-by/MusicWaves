package by.musicwaves.controller.command.action;

import by.musicwaves.controller.command.CommandException;
import by.musicwaves.entity.User;
import by.musicwaves.controller.resources.ApplicationPage;
import by.musicwaves.controller.resources.TransitType;
import by.musicwaves.entity.ancillary.Language;
import by.musicwaves.service.ServiceException;
import by.musicwaves.service.ServiceResponse;
import by.musicwaves.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginCommand extends ActionCommand
{
    private final static String PARAM_NAME_LOGIN = "login";
    private final static String PARAM_NAME_PASSWORD = "password";
    private final static String LANGUAGE_COOKIE_NAME = "language";

    private final static String SESSION_ATTRIBUTE_NAME_USER = "user";
    private static final String SESSION_ATTRIBUTE_NAME_LOCALE = "locale";

    private final static UserService service = UserService.getInstance();
    private static final Logger LOGGER = LogManager.getLogger(LoginCommand.class);


    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        this.transitType = TransitType.REDIRECT;

        String login = request.getParameter(PARAM_NAME_LOGIN);
        String password = request.getParameter(PARAM_NAME_PASSWORD);
        String language = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(LANGUAGE_COOKIE_NAME))
                .findAny()
                .map(Cookie::getValue)
                .orElse("");
        Locale locale = Language.getByNativeName(language).getLocale();

        try
        {
            // check if we have this user in our database
            ServiceResponse<User> serviceResponse = service.login(login, password, locale);

            // if login credentials are all right
            if (serviceResponse.isSuccess())
            {
                User user = serviceResponse.getStoredValue();
                HttpSession session = request.getSession();
                session.setAttribute(SESSION_ATTRIBUTE_NAME_USER, user);
                session.setAttribute(SESSION_ATTRIBUTE_NAME_LOCALE, user.getLanguage().getLocale());
                this.targetPage = ApplicationPage.PROFILE;
            }
            // if there is no user with such credentials
            else
            {
                this.targetPage = ApplicationPage.ENTRANCE;
            }

            // set  messages and error codes to be shown for user or to be processed by front-end
            attachServiceResponse(request, serviceResponse);

            LOGGER.debug("Service response is: \n" + serviceResponse);

            // going to proper page
            transfer(request, response);
            
        }
        catch(ServletException | IOException | ServiceException ex)
        {
            throw new CommandException("Failed to execute login command", ex);
        }
    }
    
}
