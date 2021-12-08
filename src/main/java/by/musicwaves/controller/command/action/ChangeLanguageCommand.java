package by.musicwaves.controller.command.action;

import by.musicwaves.controller.command.exception.CommandException;
import by.musicwaves.controller.command.util.Validator;
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
import java.io.IOException;

public class ChangeLanguageCommand extends AbstractActionCommand {

    private static final Logger LOGGER = LogManager.getLogger(ChangeLanguageCommand.class);
    private final static String PARAM_NAME_LANGUAGE_ID = "language_id";
    private final static String SESSION_ATTRIBUTE_LOCALE = "locale";
    private final UserService service = ServiceFactory.getInstance().getUserService();

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        ApplicationPage targetPage = ApplicationPage.PROFILE;
        TransitType transitType = TransitType.REDIRECT;

        String stringLanguageId = request.getParameter(PARAM_NAME_LANGUAGE_ID);
        User user = getUser(request);

        try {
            // user is not logged in (m.b. session has expired)
            if (user == null) {
                targetPage = ApplicationPage.ENTRANCE;
                transfer(request, response, targetPage, transitType);
                return;
            }

            // check if languageId is a valid Integer value
            Validator.assertIsValidInteger(stringLanguageId);
            int intLanguageId = Integer.parseInt(stringLanguageId);

            // running service
            ServiceResponse<Language> serviceResponse = service.changeLanguage(user, intLanguageId);

            // if service succeeded
            if (serviceResponse.isSuccess()) {
                Language language = serviceResponse.getStoredValue();
                // changing locale so after page will be reloaded, user could see properly localized values
                request.getSession().setAttribute(SESSION_ATTRIBUTE_LOCALE, language.getLocale());
            }

            // set  messages and error codes to be shown for user or to be processed by front-end
            attachServiceResponse(request, serviceResponse);

            // going to proper page
            transfer(request, response, targetPage, transitType);

        } catch (ServletException | IOException | ServiceException ex) {
            throw new CommandException("Failed to execute command", ex);
        }
    }
}
