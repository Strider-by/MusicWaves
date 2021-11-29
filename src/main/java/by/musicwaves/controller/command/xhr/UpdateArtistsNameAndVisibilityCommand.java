package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.CommandException;
import by.musicwaves.controller.command.Converter;
import by.musicwaves.controller.command.Validator;
import by.musicwaves.entity.Role;
import by.musicwaves.entity.User;
import by.musicwaves.service.ArtistService;
import by.musicwaves.service.ServiceException;
import by.musicwaves.service.ServiceResponse;
import by.musicwaves.util.BooleanOption;
import by.musicwaves.util.JsonSelfWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

public class UpdateArtistsNameAndVisibilityCommand extends XHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(UpdateArtistsNameAndVisibilityCommand.class);
    private final static ArtistService service = ArtistService.getInstance();

    private final static String PARAM_NAME_ID = "id";
    private final static String PARAM_NAME_NAME = "name";
    private final static String PARAM_NAME_VISIBLE = "visible";

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, CommandException {

        // user must be logged in and it must be an administrator
        User user = getUser(request);
        if (user == null || (user.getRole() != Role.ADMINISTRATOR && user.getRole() != Role.MUSIC_CURATOR)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        Locale locale = user.getLanguage().getLocale();

        int id = Converter.toInt(request.getParameter(PARAM_NAME_ID));
        // can come as a valid value, empty string or don't come at all
        // if it comes as empty string or don't come at all, store it as null
        String name = Validator.assertNonNullOrEmpty(request.getParameter(PARAM_NAME_NAME));
        Boolean visible = BooleanOption.getById( // todo: fix possible null value
                Converter.toIntegerPossiblyNullOrEmptyString(
                        request.getParameter(PARAM_NAME_VISIBLE)))
                .getValue();


        ServiceResponse<?> serviceResponse;
        try {
            serviceResponse = service.updateArtist(
                    id, name, visible, locale);
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
