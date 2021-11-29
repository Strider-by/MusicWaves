package by.musicwaves.controller.command.action;

import by.musicwaves.controller.command.CommandException;
import by.musicwaves.controller.command.Validator;
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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ChangeLanguageCommand extends ActionCommand
{
    private final static String PARAM_NAME_LANGUAGE_ID = "language_id";
    private final static String SESSION_ATTRIBUTE_NAME_USER = "user";

    private final static UserService service = UserService.getInstance();
    private static final Logger LOGGER = LogManager.getLogger(ChangeLanguageCommand.class);


    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        this.transitType = TransitType.REDIRECT;

        String stringLanguageId = request.getParameter(PARAM_NAME_LANGUAGE_ID);
        User user = (User) request.getSession().getAttribute(SESSION_ATTRIBUTE_NAME_USER);

        try {
            // user is not logged in (m.b. session has expired)
            if (user == null) {
                this.targetPage = ApplicationPage.ENTRANCE;
                transfer(request, response);
                return;
            } else {
                this.targetPage = ApplicationPage.PROFILE;
            }

            // check if languageId is a valid Integer value
            Validator.assertValidInteger(stringLanguageId);
            int intLanguageId = Integer.parseInt(stringLanguageId);

            // running service
            ServiceResponse<Language> serviceResponse = service.changeLanguage(user, intLanguageId);

            // if service succeeded
            if (serviceResponse.isSuccess()) {
                Language language = serviceResponse.getStoredValue();
                // we kind of already did it when where updating our user (Service layer)
                // so... just to know it is a required action
                // user.setLanguage(language); // done already
                // changing locale so after page will be reloaded, user could see properly localized values
                request.getSession().setAttribute("locale", user.getLanguage().getLocale());
            }

            // set  messages and error codes to be shown for user or to be processed by front-end
            attachServiceResponse(request, serviceResponse);

            LOGGER.debug("Service response is: \n" + serviceResponse);

            // going to proper page
            transfer(request, response);

        } catch(ServletException | IOException | ServiceException ex) {
            throw new CommandException("Failed to execute command", ex);
        }
    }



}
