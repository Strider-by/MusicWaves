package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.exception.CommandException;
import by.musicwaves.controller.command.exception.ValidationException;
import by.musicwaves.controller.command.util.Converter;
import by.musicwaves.controller.resource.AccessLevel;
import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.service.UserService;
import by.musicwaves.service.exception.ServiceException;
import by.musicwaves.service.factory.ServiceFactory;
import by.musicwaves.util.JsonSelfWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class ChangeUserRoleCommand extends AbstractXHRCommand {

    private final static UserService service = ServiceFactory.getInstance().getUserService();
    private final static String PARAM_NAME_USER_ID = "user_id";
    private final static String PARAM_NAME_ROLE_ID = "role_id";

    public ChangeUserRoleCommand(AccessLevel accessLevel) {
        super(accessLevel);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, ValidationException {

        Locale locale = getUser(request).getLanguage().getLocale();
        int userId = Converter.toInt(request.getParameter(PARAM_NAME_USER_ID));
        int roleId = Converter.toInt(request.getParameter(PARAM_NAME_ROLE_ID));

        ServiceResponse<?> serviceResponse;
        try {
            serviceResponse = service.changeUserRole(userId, roleId, locale);
        } catch (ServiceException ex) {
            throw new CommandException(ex);
        }

        JsonSelfWrapper json = new JsonSelfWrapper();
        json.openJson();

        appendServiceExecutionResult(serviceResponse, json);
        appendServiceMessages(serviceResponse, json);

        json.closeJson();
        sendResultJson(json, response);
    }
}
