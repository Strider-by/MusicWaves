package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.util.Converter;
import by.musicwaves.controller.command.exception.CommandException;
import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.entity.Role;
import by.musicwaves.entity.User;
import by.musicwaves.service.AudioTrackService;
import by.musicwaves.service.exception.ServiceException;
import by.musicwaves.service.factory.ServiceFactory;
import by.musicwaves.util.JsonSelfWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

public class ShiftAudioTrackAlbumPositionDownCommand extends AbstractXHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(ShiftAudioTrackAlbumPositionDownCommand.class);
    private final static AudioTrackService service = ServiceFactory.getInstance().getAudioTrackService();

    private final static String PARAM_NAME_ID = "id";

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, CommandException {

        // user must be logged in and it must be an administrator or a curator
        User user = getUser(request);
        if (user == null || (user.getRole() != Role.ADMINISTRATOR && user.getRole() != Role.MUSIC_CURATOR)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        Locale locale = user.getLanguage().getLocale();

        int id = Converter.toInt(request.getParameter(PARAM_NAME_ID));


        ServiceResponse<?> serviceResponse;
        try {
            serviceResponse = service.shiftTrackNumberDown(id, locale);
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
