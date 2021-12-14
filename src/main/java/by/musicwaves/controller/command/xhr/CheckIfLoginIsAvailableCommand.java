package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.exception.CommandException;
import by.musicwaves.controller.command.exception.ValidationException;
import by.musicwaves.controller.command.util.Validator;
import by.musicwaves.controller.resource.AccessLevel;
import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.entity.User;
import by.musicwaves.entity.ancillary.Language;
import by.musicwaves.service.UserService;
import by.musicwaves.service.exception.ServiceException;
import by.musicwaves.service.factory.ServiceFactory;
import by.musicwaves.util.JsonSelfWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Optional;

public class CheckIfLoginIsAvailableCommand extends AbstractXHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(CheckIfLoginIsAvailableCommand.class);
    private final static UserService service = ServiceFactory.getInstance().getUserService();
    private final static String PARAM_NAME_LOGIN = "login";

    public CheckIfLoginIsAvailableCommand(AccessLevel accessLevel) {
        super(accessLevel);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, ValidationException {

        User user = getUser(request);
        Locale locale = Optional.ofNullable(user)
                .map(User::getLanguage)
                .map(Language::getLocale)
                .orElse(request.getLocale());
        String login = Validator.assertNonNull(request.getParameter(PARAM_NAME_LOGIN));

        ServiceResponse<Boolean> serviceResponse;
        try {
            serviceResponse = service.checkIfLoginIsAvailable(login, locale);
        } catch (ServiceException ex) {
            throw new CommandException(ex);
        }

        JsonSelfWrapper json = new JsonSelfWrapper();
        json.openJson();

        appendServiceProvidedData(serviceResponse, json);
        appendServiceExecutionResult(serviceResponse, json);
        appendServiceMessages(serviceResponse, json);

        json.closeJson();
        sendResultJson(json, response);
    }

    private void appendServiceProvidedData(ServiceResponse<Boolean> serviceResponse, JsonSelfWrapper json) {
        // if service fails in his work, no data is provided
        if (!serviceResponse.isSuccess()) {
            return;
        }

        json.appendBoolean("login_is_available", serviceResponse.getStoredValue());
    }
}
