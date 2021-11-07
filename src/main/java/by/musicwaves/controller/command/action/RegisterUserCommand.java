package by.musicwaves.controller.command.action;

import by.musicwaves.controller.command.CommandException;
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
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RegisterUserCommand extends ActionCommand
{
    private final static String PARAM_NAME_LOGIN = "login";
    private final static String PARAM_NAME_PASSWORD_1 = "password1";
    private final static String PARAM_NAME_PASSWORD_2 = "password2";
    private final static String PARAM_NAME_INVITE_CODE = "invite_code";
    private final static String LANGUAGE_COOKIE_NAME = "language";

    private final static UserService service = UserService.getInstance();
    private static final Logger LOGGER = LogManager.getLogger(RegisterUserCommand.class);


    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException
    {
        this.transitType = TransitType.REDIRECT;
        this.targetPage = ApplicationPage.ENTRANCE;

        String login = request.getParameter(PARAM_NAME_LOGIN);
        String password1 = request.getParameter(PARAM_NAME_PASSWORD_1);
        String password2 = request.getParameter(PARAM_NAME_PASSWORD_2);
        String invite_code = request.getParameter(PARAM_NAME_INVITE_CODE);
        String cookieLanguage = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(LANGUAGE_COOKIE_NAME))
                .findAny()
                .map(Cookie::getValue)
                .orElse("");
        Language language = Language.getByNativeName(cookieLanguage);

        try
        {
            LOGGER.debug("Register user command reached");
            LOGGER.debug("provided via cookie language: " + cookieLanguage);
            Arrays.stream(request.getCookies())
                    .forEach(cookie -> LOGGER.debug("name: " + cookie.getName() + " value: " + cookie.getValue()));

            // register user if it is possible
            ServiceResponse<Integer> serviceResponse = service.registerUser(login, password1.toCharArray(), password2.toCharArray(), invite_code, language);

            // attach service response object to session to be processed in jsp if required
            attachServiceResponse(request, serviceResponse);

            LOGGER.debug("Service response is: \n" + serviceResponse);

            // going to proper page
            transfer(request, response);

        }
        catch(ServletException | IOException | ServiceException ex)
        {
            throw new CommandException("Failed to execute register User command", ex);
        }
    }

}
