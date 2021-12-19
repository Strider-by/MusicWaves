package by.musicwaves.controller.command.action;

import by.musicwaves.controller.exception.CommandException;
import by.musicwaves.controller.exception.ValidationException;
import by.musicwaves.controller.util.AccessLevelEnum;
import by.musicwaves.controller.util.ApplicationPageEnum;
import by.musicwaves.controller.util.Converter;
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
import java.io.IOException;

import static by.musicwaves.controller.util.ApplicationPageEnum.PROFILE;
import static by.musicwaves.controller.util.TransitTypeEnum.REDIRECT;

public class ChangeLanguageCommand extends AbstractActionCommand {

    private final static Logger LOGGER = LogManager.getLogger(ChangeLanguageCommand.class);
    private final static String PARAM_NAME_LANGUAGE_ID = "language_id";
    private final static String SESSION_ATTRIBUTE_LOCALE = "locale";
    private final UserService service = ServiceFactory.getInstance().getUserService();

    public ChangeLanguageCommand(AccessLevelEnum accessLevelEnum) {
        super(accessLevelEnum);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, ValidationException {
        ApplicationPageEnum targetPage = PROFILE;
        TransitTypeEnum transitTypeEnum = REDIRECT;

        try {
            User user = getUser(request);
            int languageId = Converter.toInt(request.getParameter(PARAM_NAME_LANGUAGE_ID));

            // running service
            ServiceResponse<Language> serviceResponse = service.changeLanguage(user, languageId);

            // if service succeeded
            if (serviceResponse.isSuccess()) {
                Language language = serviceResponse.getStoredValue();
                // changing locale so after page will be reloaded, user could see properly localized values
                request.getSession().setAttribute(SESSION_ATTRIBUTE_LOCALE, language.getLocale());
            }

            // set  messages and error codes to be shown for user or to be processed by front-end
            attachServiceResponse(request, serviceResponse);
            // going to proper page
            transfer(request, response, targetPage, transitTypeEnum);

        } catch (ServletException | IOException | ServiceException ex) {
            throw new CommandException("Failed to execute command", ex);
        }
    }
}
