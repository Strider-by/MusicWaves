package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.CommandException;
import by.musicwaves.controller.command.Converter;
import by.musicwaves.entity.Role;
import by.musicwaves.entity.User;
import by.musicwaves.service.ServiceException;
import by.musicwaves.service.ServiceResponse;
import by.musicwaves.service.UserService;
import by.musicwaves.util.JsonSelfWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

public class ChangeUserRoleCommand extends XHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(ChangeUserRoleCommand.class);
    private final static UserService service = UserService.getInstance();

    private final static String PARAM_NAME_USER_ID = "user_id";
    private final static String PARAM_NAME_ROLE_ID = "role_id";

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, CommandException {
        LOGGER.debug("ChangeUserRoleCommand#execute reached");

        // user must be logged in and it must be an administrator
        User user = getUser(request);
        if (user == null || user.getRole() != Role.ADMINISTRATOR) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Locale locale = user.getLanguage().getLocale();
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
        response.getWriter().write(json.toString());
    }
}
