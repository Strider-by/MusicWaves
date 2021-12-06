package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.CommandException;
import by.musicwaves.controller.command.Converter;
import by.musicwaves.controller.command.Validator;
import by.musicwaves.entity.Role;
import by.musicwaves.entity.User;
import by.musicwaves.entity.ancillary.Language;
import by.musicwaves.service.ServiceException;
import by.musicwaves.service.ServiceResponse;
import by.musicwaves.service.UserService;
import by.musicwaves.util.JsonSelfWrapper;
import by.musicwaves.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class CheckIfLoginIsAvailableCommand extends XHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(CheckIfLoginIsAvailableCommand.class);
    private final static UserService service = UserService.getInstance();

    private final static String PARAM_NAME_LOGIN = "login";

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, CommandException {
        LOGGER.debug("FindUsersCommand#execute reached");

        User user = getUser(request);

        Locale locale = Optional.ofNullable(user)
            .map(User::getLanguage)
            .map(Language::getLocale)
            .orElse(request.getLocale());


        String login = Validator.assertNonNullOrEmpty(request.getParameter(PARAM_NAME_LOGIN));

        ServiceResponse<Boolean> serviceResponse;
        try {
            serviceResponse = service.checkIfLoginIsAvailable(
                    login, locale);
        } catch (ServiceException ex) {
            throw new CommandException(ex);
        }

        JsonSelfWrapper json = new JsonSelfWrapper();
        json.openJson();

        appendServiceProvidedData(serviceResponse, json);
        appendServiceExecutionResult(serviceResponse, json);
        appendServiceMessages(serviceResponse, json);

        json.closeJson();
        response.getWriter().write(json.toString());
    }

    private void appendServiceProvidedData(ServiceResponse<Boolean> serviceResponse, JsonSelfWrapper json) {
        // if service fails in his work, no data is provided
        if (!serviceResponse.isSuccess()) {
            return;
        }

        json.appendBoolean("login_is_available", serviceResponse.getStoredValue());
    }
}